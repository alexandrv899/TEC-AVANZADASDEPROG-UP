package com.up.spa.application.services.impl;


import com.up.spa.application.exception.ResourceNotFoundException;
import com.up.spa.application.model.Cliente;
import com.up.spa.application.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

  @Mock
  private ClienteRepository clienteRepository;

  @InjectMocks
  private ClienteService clienteService;

  private Cliente cliente;

  @BeforeEach
  void setUp() {
    // Inicializar un Cliente de prueba
    cliente = Cliente.builder()
        .id("64df18b9c44e9c2c4fd1b000")
        .nombre("Cliente Test")
        .email("cliente@test.com")
        .build();
  }


  @Test
  void testFindClienteByEmail_ReturnsCliente() {
    // Arrange
    String email = "cliente@test.com";
    when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(cliente));

    // Act
    Optional<Cliente> result = clienteService.findClienteByEmail(email);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(cliente, result.get());
    verify(clienteRepository, times(1)).findByEmail(email);
  }

  // Test para findClienteByEmail cuando el cliente no existe
  @Test
  void testFindClienteByEmail_ReturnsEmpty() {
    // Arrange
    String email = "noexistente@test.com";
    when(clienteRepository.findByEmail(email)).thenReturn(Optional.empty());

    // Act
    Optional<Cliente> result = clienteService.findClienteByEmail(email);

    // Assert
    assertFalse(result.isPresent());
    verify(clienteRepository, times(1)).findByEmail(email);
  }


  @Test
  void testAddTurnoToCliente_Success() {
    // Arrange
    String turnoId = "64df18b9c44e9c2c4fd1b111";

    // Act
    clienteService.addTurnoToCliente(cliente, turnoId);

    // Assert
    ArgumentCaptor<String> clienteIdCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> turnoIdCaptor = ArgumentCaptor.forClass(String.class);
    verify(clienteRepository, times(1)).addTurnoToCliente(clienteIdCaptor.capture(), turnoIdCaptor.capture());
    assertEquals(cliente.getId(), clienteIdCaptor.getValue());
    assertEquals(turnoId, turnoIdCaptor.getValue());
  }


  @Test
  void testObtenerClientePorEmail_ReturnsCliente() {
    // Arrange
    String email = "cliente@test.com";
    when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(cliente));

    // Act
    Cliente result = clienteService.obtenerClientePorEmail(email);

    // Assert
    assertNotNull(result);
    assertEquals(cliente, result);
    verify(clienteRepository, times(1)).findByEmail(email);
  }


  @Test
  void testObtenerClientePorEmail_ThrowsException() {
    // Arrange
    String email = "noexistente@test.com";
    when(clienteRepository.findByEmail(email)).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      clienteService.obtenerClientePorEmail(email);
    });

    assertEquals("No se encontró el cliente con email: " + email, exception.getMessage());
    verify(clienteRepository, times(1)).findByEmail(email);
  }
}
