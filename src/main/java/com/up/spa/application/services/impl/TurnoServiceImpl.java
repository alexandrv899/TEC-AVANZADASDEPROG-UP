package com.up.spa.application.services.impl;

import com.up.spa.application.component.CancelacionStrategy;
import com.up.spa.application.component.TurnoValidator;
import com.up.spa.application.exception.ResourceNotFoundException;
import com.up.spa.application.exception.TurnoNoDisponibleException;
import com.up.spa.application.model.*;
import com.up.spa.application.model.dto.request.TurnoDTO;
import com.up.spa.application.model.mappers.TurnoMapper;
import com.up.spa.application.repository.TurnoRepository;
import com.up.spa.application.services.*;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@Slf4j
public class TurnoServiceImpl implements ITurnoService {

  private final TurnoRepository turnoRepository;
  private final ISucursalService sucursalService;
  private final IClienteService clienteService;
  private final TurnoMapper turnoMapper;
  private final TurnoValidator turnoValidator;
  private final Map<String, CancelacionStrategy> estrategiasCancelacion;
  private final DisponibilidadValidatorService disponibilidadValidator;
  private final ServicioAsignacionProfesional servicioAsignacionProfesional;
  private final IHorarioService horarioService;

  public TurnoServiceImpl(
      TurnoRepository turnoRepository,
      ISucursalService sucursalService,
      IClienteService clienteService,
      TurnoMapper turnoMapper,
      TurnoValidator turnoValidator,
      @Qualifier("estrategiasCancelacion") Map<String, CancelacionStrategy> estrategiasCancelacion,
      DisponibilidadValidatorService disponibilidadValidator, ServicioAsignacionProfesional servicioAsignacionProfesional,
      IHorarioService horarioService) {
    this.turnoRepository = turnoRepository;
    this.sucursalService = sucursalService;
    this.clienteService = clienteService;
    this.turnoMapper = turnoMapper;
    this.turnoValidator = turnoValidator;
    this.estrategiasCancelacion = estrategiasCancelacion;
    this.disponibilidadValidator = disponibilidadValidator;
    this.servicioAsignacionProfesional = servicioAsignacionProfesional;
    this.horarioService = horarioService;
  }


  @Override
  public void cancelarTurno(String turnoId, String email, String rol) {
    Turno turno = turnoRepository.findById(turnoId)
        .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado"));

    CancelacionStrategy estrategia = estrategiasCancelacion.get(rol);
    if (estrategia == null) {
      throw new TurnoNoDisponibleException("Rol de usuario no autorizado para cancelar turnos.");
    }

    estrategia.cancelarTurno(turno, email);
    turnoRepository.save(turno);
  }

  @Override
  public List<Turno> listarTurnos(String email, String rol) {
    return switch (rol.toUpperCase()) {
      case "ROLE_PROFESSIONAL" -> turnoRepository.findAllGroupedByCliente();
      case "ROLE_CLIENT" -> clienteService.findClienteByEmail(email)
          .map(cliente -> turnoRepository.findAllByClienteId(cliente.getId()))
          .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado!"));
      default -> throw new IllegalArgumentException("Rol inválido: " + rol);
    };
  }

  @Override
  public TurnoDTO crearTurno(final TurnoDTO turnoDto) {
    log.info("Creando turno para el cliente: {}", turnoDto.getEmail());

    Sucursal sucursal = sucursalService.findById(turnoDto.getSucursalId())
        .orElseThrow(() -> new ResourceNotFoundException("No se encontró la sucursal con ID: " + turnoDto.getSucursalId()));

    turnoValidator.validateTurnoData(turnoDto, sucursal);
    Cliente cliente = clienteService.obtenerClientePorEmail(turnoDto.getEmail());

    Servicio servicio = sucursalService.obtenerServicioDeSucursal(turnoDto.getServicioId(), sucursal)
        .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

    String horaFin = horarioService.calcularHoraFin(turnoDto.getHora(), servicio.getDuracion());
    disponibilidadValidator.validarDisponibilidadCliente(cliente, turnoDto.getFecha(), turnoDto.getHora(), horaFin);

    Profesional profesional = obtenerProfesionalDisponible(sucursal, turnoDto, servicio, horaFin);

    Turno turno = turnoMapper.toEntity(turnoDto);
    turno.setCliente(cliente);
    turno.setProfesional(profesional);
    turno.setProfesionalSeleccionadoPorCliente(turnoDto.getProfesionalId() != null);

    String id = turnoRepository.save(turno);
    turnoDto.setId(id);
    turnoDto.setProfesionalId(profesional.getId());

    clienteService.addTurnoToCliente(cliente, id);
    log.info("Turno creado exitosamente con ID: {}", id);
    return turnoDto;
  }

  private Profesional obtenerProfesionalDisponible(Sucursal sucursal, TurnoDTO turnoDto, Servicio servicio, String horaFin) {
    // Validar que la especialidad requerida esté disponible en la sucursal
    validarEspecialidadDisponible(sucursal, servicio);

    // Si el cliente seleccionó un profesional específico, obtener ese profesional
    if (turnoDto.getProfesionalId() != null) {
      return obtenerProfesionalEspecifico(turnoDto, sucursal, servicio, horaFin);
    }

    // Llamar a obtenerProfesionalGenerico en ServicioAsignacionProfesional usando IDs y otros datos en formato String
    return servicioAsignacionProfesional.obtenerProfesionalGenerico(
        sucursal.getId(),            // Sucursal ID
        servicio.getId(),            // Servicio ID
        turnoDto.getFecha(),         // Fecha como String
        turnoDto.getHora(),          // Hora de inicio como String
        horaFin                      // Hora de fin como String
    );
  }

  private void validarEspecialidadDisponible(Sucursal sucursal, Servicio servicio) {
    boolean especialidadDisponible = sucursal.getEspecialidades().stream()
        .anyMatch(especialidad -> especialidad.getId().equals(servicio.getEspecialidadId()));

    if (!especialidadDisponible) {
      throw new TurnoNoDisponibleException("La especialidad requerida no está disponible en la sucursal.");
    }
  }

  private Profesional obtenerProfesionalEspecifico(TurnoDTO turnoDto, Sucursal sucursal, Servicio servicio, String horaFin) {
    Optional<Profesional> profesionalOpt = sucursal.getProfesionales().stream()
        .filter(prof -> prof.getId().equals(turnoDto.getProfesionalId()))
        .findFirst();

    if (profesionalOpt.isEmpty()) {
      throw new TurnoNoDisponibleException("El profesional especificado no está asociado a la sucursal.");
    }

    if (!disponibilidadValidator.puedeRealizarServicio(profesionalOpt.get(), servicio)) {
      throw new TurnoNoDisponibleException("El profesional especificado no cuenta con la especialidad requerida.");
    }

    if (disponibilidadValidator.validarDisponibilidadProfesional(profesionalOpt.get(), turnoDto.getFecha(), turnoDto.getHora(), horaFin)) {
      return profesionalOpt.get();
    } else {
      throw new TurnoNoDisponibleException("El profesional especificado no está disponible en el horario solicitado.");
    }
  }

}
