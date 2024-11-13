package com.up.spa.application.component;

import com.up.spa.application.exception.TurnoNoDisponibleException;
import com.up.spa.application.model.Profesional;
import com.up.spa.application.model.Turno;
import com.up.spa.application.services.IHorarioService;
import com.up.spa.application.services.ServicioAsignacionProfesional;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ROLE_PROFESSIONAL")
public class CancelacionProfesionalStrategy implements CancelacionStrategy {

  private static final String CANCELADO_PROFESIONAL = "CANCELADO_PROFESIONAL";
  private final ServicioAsignacionProfesional servicioAsignacionProfesional;
  private final IHorarioService horarioService;

  public CancelacionProfesionalStrategy(ServicioAsignacionProfesional servicioAsignacionProfesional, IHorarioService horarioService) {
    this.servicioAsignacionProfesional = servicioAsignacionProfesional;
    this.horarioService = horarioService;
  }

  @Override
  public void cancelarTurno(Turno turno, String email) throws TurnoNoDisponibleException {
    if (turno.isProfesionalSeleccionadoPorCliente()) {
      cancelarPorProfesionalSeleccionado(turno);
      return;
    }
    cancelarPorAsignacionAutomatica(turno);
  }

  private void cancelarPorProfesionalSeleccionado(Turno turno) {
    turno.setEstado(CANCELADO_PROFESIONAL);
  }

  private void cancelarPorAsignacionAutomatica(Turno turno) {
    String fechaStr = convertirFechaATexto(turno);
    String horaFin = horarioService.calcularHoraFin(turno.getHora(), turno.getServicio().getDuracion());

    try {
      // Intentar obtener un profesional disponible
      Profesional nuevoProfesional = servicioAsignacionProfesional.obtenerProfesionalGenerico(
          turno.getSucursal().getId(),          // ID de la sucursal
          turno.getServicio().getId(),          // ID del servicio
          fechaStr,                             // Fecha en formato String
          turno.getHora(),                      // Hora de inicio en formato String
          horaFin                               // Hora de fin en formato String
      );

      turno.setProfesional(nuevoProfesional);

    } catch (TurnoNoDisponibleException e) {
      // Si no hay profesionales disponibles, cambiar el estado a CANCELADO_PROFESIONAL
      turno.setEstado(CANCELADO_PROFESIONAL);
    }
  }


  private String convertirFechaATexto(Turno turno) {
    return turno.getFecha().toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }
}
