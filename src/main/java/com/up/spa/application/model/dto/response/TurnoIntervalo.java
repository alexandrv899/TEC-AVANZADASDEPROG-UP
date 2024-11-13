package com.up.spa.application.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TurnoIntervalo {
  private String horaInicio;
  private String horaFin;
  private String estado; // "DISPONIBLE" o "OCUPADO"
}