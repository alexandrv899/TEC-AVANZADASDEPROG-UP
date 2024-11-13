package com.up.spa.application.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Profesional {
  private String id;
  private String nombre;
  private String email;
  private List<String> especialidadIds;
  private List<Horario> horarios;
  private List<String> sucursalIds;
}