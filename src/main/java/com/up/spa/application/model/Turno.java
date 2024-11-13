package com.up.spa.application.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Turno {

  private String id;
  private Date fecha;
  private String hora;
  private String estado;
  private Cliente cliente;
  private Servicio servicio;
  private Profesional profesional;
  private Sucursal sucursal;
  private boolean profesionalSeleccionadoPorCliente;


}
