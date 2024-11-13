package com.up.spa.application.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TurnoSugerencia {
  private String profesionalId;
  private String nombreProfesional;
  private String nombreServicio;
  private String diaDisponible;
}
