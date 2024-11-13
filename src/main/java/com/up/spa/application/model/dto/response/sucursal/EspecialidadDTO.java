package com.up.spa.application.model.dto.response.sucursal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.up.spa.application.model.Especialidad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EspecialidadDTO {

  private String id;
  private String especialidad;
  private String descripcion;


  public static EspecialidadDTO fromEspecialidad(Especialidad especialidad) {
    return EspecialidadDTO.builder()
        .id(especialidad.getId())
        .especialidad(especialidad.getNombre())
        .descripcion(especialidad.getDescripcion())
        .build();
  }

}
