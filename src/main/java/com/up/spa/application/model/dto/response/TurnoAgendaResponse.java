package com.up.spa.application.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TurnoAgendaResponse {
  private String profesionalId;
  private String nombreProfesional;
  private String nombreServicio;
  private int duracionServicio;
  private List<TurnoIntervalo> intervalos;
}
