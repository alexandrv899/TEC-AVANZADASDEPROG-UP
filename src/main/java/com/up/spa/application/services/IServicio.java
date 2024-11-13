package com.up.spa.application.services;

import com.up.spa.application.model.Servicio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Optional;

public interface IServicio {

  Servicio findServicioById(String servicioId);

  int obtenerDuracionServicio(String servicioId);

}