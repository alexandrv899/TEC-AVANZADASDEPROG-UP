package com.up.spa.application.services;

import com.up.spa.application.model.Servicio;
import com.up.spa.application.model.Sucursal;
import java.util.List;
import java.util.Optional;

public interface ISucursalService {

  List<Sucursal> getAllSucursales();

  Optional<Sucursal> findById(String sucursalId);

  Optional<Servicio> obtenerServicioDeSucursal(String serviceId, Sucursal sucursall);
}
