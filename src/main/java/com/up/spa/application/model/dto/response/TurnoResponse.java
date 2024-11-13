package com.up.spa.application.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.up.spa.application.enums.EstadoTurno;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TurnoResponse {

  private String id;
  private LocalDateTime fechaHora;
  private String sucursalNombre;
  private String servicioNombre;
  private String profesionalNombre;
  private EstadoTurno estado;

}
