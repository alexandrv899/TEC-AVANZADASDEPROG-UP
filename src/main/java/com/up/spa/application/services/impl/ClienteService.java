package com.up.spa.application.services.impl;

import com.up.spa.application.exception.ResourceNotFoundException;
import com.up.spa.application.model.Cliente;
import com.up.spa.application.repository.ClienteRepository;
import com.up.spa.application.services.IClienteService;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ClienteService implements IClienteService {

  private final ClienteRepository clienteRepository;

  public ClienteService(ClienteRepository clienteRepository) {
    this.clienteRepository = clienteRepository;
  }

  @Override
  public Optional<Cliente> findClienteByEmail(String email) {
    return clienteRepository.findByEmail(email);
  }

  @Override
  public void addTurnoToCliente(Cliente cliente, String turnoId) {
      clienteRepository.addTurnoToCliente(cliente.getId(), turnoId);
  }

  @Override
  public Cliente obtenerClientePorEmail(String email) {
    return findClienteByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("No se encontró el cliente con email: " + email));
  }
}
