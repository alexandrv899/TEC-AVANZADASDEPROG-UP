package com.up.spa.application.model.dto.response.sucursal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.up.spa.application.model.Especialidad;
import com.up.spa.application.model.Servicio;
import com.up.spa.application.model.Sucursal;
import java.util.ArrayList;
import java.util.List;
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
public class SucursalResponse {

  private String id;
  private String nombre;
  private String direccion;

  private List<ProfesionalDTO> profesionales;
  private List<Servicio> servicios;
  private List<Especialidad> especialidades;
  private List<HorarioDTO> horarios;


  public static SucursalResponse fromModel(Sucursal sucursal) {
    return SucursalResponse.builder()
        .id(sucursal.getId())
        .nombre(sucursal.getNombre())
        .direccion(sucursal.getDireccion())
        .profesionales(Optional.ofNullable(sucursal.getProfesionales())
            .map(profs -> profs.stream()
                .map(p -> ProfesionalDTO.fromProfesional(p, sucursal.getEspecialidades()))
                .collect(Collectors.toList()))
            .orElse(new ArrayList<>()))
        .servicios(Optional.ofNullable(sucursal.getServicios())
            .orElse(new ArrayList<>()))
        .especialidades(Optional.ofNullable(sucursal.getEspecialidades())
            .orElse(new ArrayList<>()))
        .horarios(Optional.ofNullable(sucursal.getHorarios())
            .map(horarios -> horarios.stream()
                .map(HorarioDTO::fromHorario)
                .collect(Collectors.toList()))
            .orElse(new ArrayList<>()))
        .build();
  }
}