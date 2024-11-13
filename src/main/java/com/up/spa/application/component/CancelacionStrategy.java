package com.up.spa.application.component;

import com.up.spa.application.exception.TurnoNoDisponibleException;
import com.up.spa.application.model.Turno;

public interface CancelacionStrategy {
  void cancelarTurno(Turno turno, String email) throws TurnoNoDisponibleException;
}
