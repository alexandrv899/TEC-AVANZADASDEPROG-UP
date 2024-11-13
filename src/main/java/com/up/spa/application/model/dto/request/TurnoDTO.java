package com.up.spa.application.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TurnoDTO {

  private String id;

  @NotNull(message = "La fecha es requerida")
  @Pattern(regexp = "^(19|20)\\d\\d-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$", message = "El formato de fecha es inválido (yyyy-MM-dd)")
  private String fecha;

  @NotNull(message = "La hora es requerida")
  @Pattern(regexp = "^([01][0-9]|2[0-3]):[0-5][0-9]$", message = "El formato de hora es inválido (HH:mm)")
  private String hora;

  private String estado;
  private String email;

  @NotBlank(message = "El ID del servicio es requerido")
  @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "El ID del servicio es inválido")
  private String servicioId;

  @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "El ID del profesional es inválido")
  private String profesionalId;

  @NotBlank(message = "El ID de la sucursal es requerido")
  @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "El ID de la sucursal es inválido")
  private String sucursalId;

  private boolean profesionalSeleccionadoPorCliente;
}
