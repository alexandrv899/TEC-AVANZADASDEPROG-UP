package com.up.spa.auth.application.dto.request;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Data
public class AuthRequest {

  @Email(message = "Formato de email inválido")
  @NotBlank(message = "El email no puede estar vacío")
  private String email;

  @NotBlank(message = "La contraseña no puede estar vacía")
  @Size(min = 6, max = 50, message = "La contraseña debe tener entre 6 y 50 caracteres")
  private String password;

}
