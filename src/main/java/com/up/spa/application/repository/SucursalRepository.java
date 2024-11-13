package com.up.spa.application.repository;

import com.up.spa.application.model.Sucursal;
import java.util.List;
import java.util.Optional;

public interface SucursalRepository{

  List<Sucursal> findAllSucursales();
  Optional<Sucursal> findById(String sucursalId);

}

