package com.up.spa.application.services;

import com.up.spa.application.exception.TurnoNoDisponibleException;
import com.up.spa.application.model.Cliente;
import com.up.spa.application.model.Profesional;
import com.up.spa.application.model.Servicio;
import com.up.spa.application.repository.TurnoRepository;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;


@Service
public class DisponibilidadValidatorService {

  private final TurnoRepository turnoRepository;
  private final IHorarioService horarioService;

  public DisponibilidadValidatorService(TurnoRepository turnoRepository, IHorarioService horarioService) {
    this.turnoRepository = turnoRepository;
    this.horarioService = horarioService;
  }

  public void validarDisponibilidadCliente(Cliente cliente, String fecha, String horaInicio, String horaFin) {
    if (turnoRepository.isClienteOcupado(cliente.getId(), fecha, horaInicio, horaFin)) {
      throw new TurnoNoDisponibleException("El cliente ya tiene un turno en el mismo día y horario.");
    }
  }

  public boolean puedeRealizarServicio(Profesional profesional, Servicio servicio) {
    return profesional.getEspecialidadIds().stream()
        .anyMatch(especialidad -> especialidad.equals(servicio.getEspecialidadId()));
  }

  public boolean validarDisponibilidadProfesional(Profesional profesional, String fecha, String horaInicio, String horaFin) {
    String diaSemana = horarioService.obtenerDiaSemana(fecha);

    // Definir un formateador para la hora en caso de que el formato sea 'HH:mm'
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    // Convertir las horas de inicio y fin a LocalTime para facilitar la comparación
    LocalTime horaInicioTurno = LocalTime.parse(horaInicio, timeFormatter);
    LocalTime horaFinTurno = LocalTime.parse(horaFin, timeFormatter);

    return horarioService.obtenerHorariosPorProfesional(profesional.getId()).stream()
        .filter(horario -> horario.getDiaSemana().equalsIgnoreCase(diaSemana))
        .anyMatch(horario -> {
          LocalTime horaInicioHorario = LocalTime.parse(horario.getHoraInicio(), timeFormatter);
          LocalTime horaFinHorario = LocalTime.parse(horario.getHoraFin(), timeFormatter);
          return !horaInicioTurno.isBefore(horaInicioHorario) && !horaFinTurno.isAfter(horaFinHorario);
        }) && !turnoRepository.isProfesionalOcupado(profesional.getId(), fecha, horaInicio, horaFin);
  }
}
