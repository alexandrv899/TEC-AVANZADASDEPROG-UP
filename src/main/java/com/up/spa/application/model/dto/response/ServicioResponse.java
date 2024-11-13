package com.up.spa.application.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServicioResponse {
  private String id;
  private String nombre;
  private int duracion;
  private String especialidadNombre;
}
