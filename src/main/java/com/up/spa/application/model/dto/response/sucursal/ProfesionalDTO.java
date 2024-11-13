package com.up.spa.application.model.dto.response.sucursal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.up.spa.application.model.Especialidad;
import com.up.spa.application.model.Profesional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfesionalDTO {

  private String id;
  private String nombre;
  private String email;
  private List<EspecialidadDTO> especialidadIds;

  public static ProfesionalDTO fromProfesional(Profesional profesional, List<Especialidad> especialidades) {

    Map<String, Especialidad> especialidadMap = especialidades.stream()
        .collect(Collectors.toMap(Especialidad::getId, especialidad -> especialidad));

    return ProfesionalDTO.builder()
        .id(profesional.getId())
        .nombre(profesional.getNombre())
        .email(profesional.getEmail())
        .especialidadIds(Optional.ofNullable(profesional.getEspecialidadIds())
            .map(espIds -> espIds.stream()
                // Buscar la especialidad en el Map por ID
                .map(espId -> especialidadMap.get(espId))
                .filter(Objects::nonNull) // Eliminar especialidades no encontradas
                .map(EspecialidadDTO::fromEspecialidad)
                .collect(Collectors.toList()))
            .orElse(new ArrayList<>()))
        .build();
  }
}
