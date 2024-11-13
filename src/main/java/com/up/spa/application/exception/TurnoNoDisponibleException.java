package com.up.spa.application.exception;

import com.up.spa.application.model.dto.response.TurnoSugerencia;
import java.util.List;

public class TurnoNoDisponibleException extends RuntimeException {

  private List<TurnoSugerencia> sugerencias;

  public TurnoNoDisponibleException(String message) {
    super(message);
  }

  public TurnoNoDisponibleException(String message, List<TurnoSugerencia> sugerencias) {
    super(message);
    this.sugerencias = sugerencias;
  }

  public List<TurnoSugerencia> getSugerencias() {
    return sugerencias;
  }
}
