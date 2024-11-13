package com.up.spa.application.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sucursal {
  private String id;
  private String nombre;
  private String direccion;
  private List<Profesional> profesionales;
  private List<Servicio> servicios;
  private List<Especialidad> especialidades;
  private List<Horario> horarios;
}

