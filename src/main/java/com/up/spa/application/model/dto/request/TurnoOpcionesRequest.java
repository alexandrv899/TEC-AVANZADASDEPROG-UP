package com.up.spa.application.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TurnoOpcionesRequest {

  @NotBlank(message = "El ID de la sucursal es requerido")
  @Size(min = 1, max = 50, message = "El ID de la sucursal debe tener entre 1 y 50 caracteres")
  private String sucursalId;

  @NotNull(message = "La fecha es requerida")
  @Pattern(regexp = "^(19|20)\\d\\d-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$", message = "El formato de fecha es inválido (yyyy-MM-dd)")
  private String fecha;  // Formato "yyyy-MM-dd"

  @NotBlank(message = "El ID del servicio es requerido")
  @Size(min = 1, max = 50, message = "El ID del servicio debe tener entre 1 y 50 caracteres")
  private String servicioId;
}
