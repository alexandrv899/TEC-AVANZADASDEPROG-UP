package com.up.spa.application.component;

import com.up.spa.application.exception.TurnoNoDisponibleException;
import com.up.spa.application.model.Cliente;
import com.up.spa.application.model.Turno;
import com.up.spa.application.services.IClienteService;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ROLE_CLIENT")
public class CancelacionClienteStrategy implements CancelacionStrategy {

  private static final String CANCELADO_CLIENTE = "CANCELADO_CLIENTE";
  private final IClienteService clienteService;

  public CancelacionClienteStrategy(IClienteService clienteService) {
    this.clienteService = clienteService;
  }

  @Override
  public void cancelarTurno(Turno turno, String email) throws TurnoNoDisponibleException {
    Cliente cliente = clienteService.findClienteByEmail(email)
        .orElseThrow(() -> new AccessDeniedException("Cliente no encontrado!"));

    // Verifica si el cliente tiene el turno a cancelar
    if (cliente.getTurnos().stream().noneMatch(t -> t.getId().equals(turno.getId()))) {
      throw new AccessDeniedException("No autorizado: Un cliente no puede cancelar el turno de otro cliente.");
    }

    LocalDate fechaTurno = turno.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    if (fechaTurno.isAfter(LocalDate.now().plusDays(1))) {
      turno.setEstado(CANCELADO_CLIENTE);
    } else {
      throw new TurnoNoDisponibleException("El turno debe cancelarse con más de 24 horas de anticipación.");
    }
  }

}