package com.up.spa.application.repository;
import com.up.spa.application.model.Cliente;

import java.util.Optional;

public interface ClienteRepository {
  Optional<Cliente> findByEmail(String email);
  void addTurnoToCliente(String clienteId, String turnoId);
}
