package com.up.spa.application.services.impl;

import com.up.spa.application.model.Servicio;
import com.up.spa.application.repository.ServicioRepository;
import com.up.spa.application.services.IServicio;
import org.springframework.stereotype.Service;

@Service
public class ServicioServiceImpl implements IServicio {

  private final ServicioRepository servicioRepository;

  public ServicioServiceImpl(ServicioRepository servicioRepository) {
    this.servicioRepository = servicioRepository;
  }

  @Override
  public Servicio findServicioById(String servicioId) {
    return servicioRepository.findServicioById(servicioId);
  }

  @Override
  public int obtenerDuracionServicio(String servicioId) {
    Servicio servicio = servicioRepository.findServicioById(servicioId);
    return servicio != null ? servicio.getDuracion() : 0;
  }

}
