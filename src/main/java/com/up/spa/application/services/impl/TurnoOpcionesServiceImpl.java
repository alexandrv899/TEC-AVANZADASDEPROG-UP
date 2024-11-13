package com.up.spa.application.services.impl;

import com.up.spa.application.exception.ResourceNotFoundException;
import com.up.spa.application.exception.TurnoNoDisponibleException;
import com.up.spa.application.model.Horario;
import com.up.spa.application.model.Profesional;
import com.up.spa.application.model.Servicio;
import com.up.spa.application.model.Sucursal;
import com.up.spa.application.model.dto.request.TurnoOpcionesRequest;
import com.up.spa.application.model.dto.response.TurnoAgendaResponse;
import com.up.spa.application.model.dto.response.TurnoIntervalo;
import com.up.spa.application.model.dto.response.TurnoSugerencia;
import com.up.spa.application.repository.SucursalRepository;
import com.up.spa.application.repository.TurnoRepository;
import com.up.spa.application.services.IServicio;
import com.up.spa.application.services.ITurnoOpcionesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TurnoOpcionesServiceImpl implements ITurnoOpcionesService {

  private static final String DISPONIBLE = "DISPONIBLE";
  private static final String OCUPADO = "OCUPADO";
  private final SucursalRepository sucursalRepository;
  private final TurnoRepository turnoRepository;
  private final IServicio servicioService;

  @Override
  public List<TurnoAgendaResponse> obtenerOpcionesTurnos(TurnoOpcionesRequest request) {
    Sucursal sucursal = obtenerSucursal(request.getSucursalId());
    Servicio servicio = obtenerServicio(request.getServicioId());

    int duracionServicio = servicio.getDuracion();
    List<TurnoAgendaResponse> agenda = new ArrayList<>();

    String diaSemanaEsp = obtenerDiaSemana(request.getFecha());

    for (Profesional profesional : sucursal.getProfesionales()) {
      if (!profesionalTieneEspecialidad(profesional, servicio)) continue;

      List<Horario> horariosDeTrabajo = obtenerHorariosDeTrabajo(profesional, diaSemanaEsp);

      if (horariosDeTrabajo.isEmpty()) continue;

      List<TurnoIntervalo> intervalos = generarIntervalosDisponibles(horariosDeTrabajo, duracionServicio, profesional, request.getFecha());
      agenda.add(new TurnoAgendaResponse(profesional.getId(), profesional.getNombre(), servicio.getNombre(), duracionServicio, intervalos));
    }

    if (agenda.isEmpty()) {
      List<TurnoSugerencia> sugerencias = buscarDiasAlternativos(sucursal, servicio);
      throw new TurnoNoDisponibleException("No hay turnos disponibles en la fecha solicitada. Pruebe en los siguientes días:", sugerencias);
    }

    return agenda;
  }

  private Sucursal obtenerSucursal(String sucursalId) {
    return sucursalRepository.findById(sucursalId)
        .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + sucursalId));
  }

  private Servicio obtenerServicio(String servicioId) {
    Servicio servicio = servicioService.findServicioById(servicioId);
    if (servicio == null) {
      throw new ResourceNotFoundException("Servicio no encontrado con ID: " + servicioId);
    }
    return servicio;
  }

  private String obtenerDiaSemana(String fecha) {
    DayOfWeek diaSemana = LocalDate.parse(fecha).getDayOfWeek();
    return diaSemana.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
  }

  private boolean profesionalTieneEspecialidad(Profesional profesional, Servicio servicio) {
    return profesional.getEspecialidadIds().contains(servicio.getEspecialidadId());
  }

  private List<Horario> obtenerHorariosDeTrabajo(Profesional profesional, String diaSemanaEsp) {
    return profesional.getHorarios().stream()
        .filter(horario -> horario.getDiaSemana().equalsIgnoreCase(diaSemanaEsp))
        .collect(Collectors.toList());
  }

  private List<TurnoIntervalo> generarIntervalosDisponibles(List<Horario> horariosDeTrabajo, int duracionServicio, Profesional profesional, String fecha) {
    List<TurnoIntervalo> intervalos = new ArrayList<>();
    List<LocalTime[]> intervalosOcupados = turnoRepository.getIntervalosOcupados(profesional.getId(), fecha);

    for (Horario horario : horariosDeTrabajo) {
      LocalTime cursor = LocalTime.parse(horario.getHoraInicio());
      LocalTime horaFin = LocalTime.parse(horario.getHoraFin());

      while (cursor.plusMinutes(duracionServicio).isBefore(horaFin) || cursor.plusMinutes(duracionServicio).equals(horaFin)) {
        LocalTime finIntervalo = cursor.plusMinutes(duracionServicio);
        LocalTime finalCursor = cursor;
        boolean ocupado = intervalosOcupados.stream()
            .anyMatch(intervalo -> finalCursor.isBefore(intervalo[1]) && finIntervalo.isAfter(intervalo[0]));

        intervalos.add(new TurnoIntervalo(cursor.toString(), finIntervalo.toString(), ocupado ? OCUPADO : DISPONIBLE));
        cursor = finIntervalo;
      }
    }
    return intervalos;
  }

  private List<TurnoSugerencia> buscarDiasAlternativos(Sucursal sucursal, Servicio servicio) {
    List<TurnoSugerencia> sugerencias = new ArrayList<>();

    for (Profesional profesional : sucursal.getProfesionales()) {
      if (!profesionalTieneEspecialidad(profesional, servicio)) continue;

      for (Horario horario : profesional.getHorarios()) {
        sugerencias.add(new TurnoSugerencia(profesional.getId(), profesional.getNombre(), servicio.getNombre(), horario.getDiaSemana()));
      }
    }
    return sugerencias;
  }
}
