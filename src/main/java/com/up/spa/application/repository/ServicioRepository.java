package com.up.spa.application.repository;

import com.up.spa.application.model.Servicio;

public interface ServicioRepository {

  Servicio findServicioById(String servicioId);

}
