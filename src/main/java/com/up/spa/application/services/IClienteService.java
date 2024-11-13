package com.up.spa.application.services;

import com.up.spa.application.model.Cliente;
import java.util.Optional;

public interface IClienteService {

  Optional<Cliente> findClienteByEmail(String email);

  void addTurnoToCliente(Cliente cliente, String turnoId);

  Cliente obtenerClientePorEmail(String email);
}