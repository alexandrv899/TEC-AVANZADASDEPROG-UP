package com.up.spa.application.model.dto.response.sucursal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.up.spa.application.model.Horario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HorarioDTO {

  private String diaSemana;
  private String horaInicio;
  private String horaFin;

  public static HorarioDTO fromHorario(Horario horario) {
    return HorarioDTO.builder()
        .diaSemana(horario.getDiaSemana())
        .horaInicio(horario.getHoraInicio())
        .horaFin(horario.getHoraFin())
        .build();
  }
}
