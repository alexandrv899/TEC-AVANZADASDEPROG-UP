package com.up.spa.application.services.impl;

import com.up.spa.application.model.Servicio;
import com.up.spa.application.model.Sucursal;
import com.up.spa.application.repository.SucursalRepository;
import com.up.spa.application.services.ISucursalService;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class SucursalServiceImpl implements ISucursalService {

  private final SucursalRepository sucursalRepository;

  public SucursalServiceImpl(SucursalRepository sucursalRepository) {
    this.sucursalRepository = sucursalRepository;
  }

  @Override
  public List<Sucursal> getAllSucursales() {
    return sucursalRepository.findAllSucursales();
  }

  @Override
  public Optional<Sucursal> findById(String id) {
    return sucursalRepository.findById(id);
  }



  @Override
  public Optional<Servicio> obtenerServicioDeSucursal(String serviceId, Sucursal sucursal) {
    return sucursal.getServicios().stream()
        .filter(servicio -> servicio.getId().equals(serviceId))
        .findFirst();
  }
}
