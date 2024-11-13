package com.up.spa.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.up.spa.application.exception.ResourceNotFoundException;
import com.up.spa.application.model.Sucursal;
import com.up.spa.application.model.dto.response.sucursal.SucursalResponse;
import com.up.spa.application.services.ISucursalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class SucursalControllerTest {

  @Mock
  private ISucursalService sucursalService;

  @InjectMocks
  private SucursalController sucursalController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetAllSucursales_ReturnsSucursalList() {
    // Arrange
    Sucursal sucursal1 = Sucursal.builder().id("1").nombre("Sucursal 1").direccion("Direccion 1").build();
    Sucursal sucursal2 = Sucursal.builder().id("2").nombre("Sucursal 2").direccion("Direccion 2").build();
    when(sucursalService.getAllSucursales()).thenReturn(Arrays.asList(sucursal1, sucursal2));

    // Act
    ResponseEntity<List<SucursalResponse>> response = sucursalController.getAllSucursales();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().size());
    verify(sucursalService, times(1)).getAllSucursales();
  }

  @Test
  void testGetAllSucursales_ReturnsNoContent() {
    // Arrange
    when(sucursalService.getAllSucursales()).thenReturn(Collections.emptyList());

    // Act
    ResponseEntity<List<SucursalResponse>> response = sucursalController.getAllSucursales();

    // Assert
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(sucursalService, times(1)).getAllSucursales();
  }

  @Test
  void testGetSucursalById_ReturnsSucursal() {
    // Arrange
    String sucursalId = "64df18b9c44e9c2c4fd1a999";
    Sucursal sucursal = Sucursal.builder().id(sucursalId).nombre("Sucursal 1").direccion("Direccion 1").build();
    when(sucursalService.findById(sucursalId)).thenReturn(Optional.of(sucursal));

    // Act
    ResponseEntity<SucursalResponse> response = sucursalController.getSucursalById(sucursalId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(sucursalId, response.getBody().getId());
    assertEquals("Sucursal 1", response.getBody().getNombre());
    verify(sucursalService, times(1)).findById(sucursalId);
  }

  @Test
  void testGetSucursalById_ThrowsResourceNotFoundException() {
    // Arrange
    String sucursalId = "64df18b9c44e9c2c4fd1a999";
    when(sucursalService.findById(sucursalId)).thenReturn(Optional.empty());

    // Act & Assert
    try {
      sucursalController.getSucursalById(sucursalId);
    } catch (ResourceNotFoundException ex) {
      assertEquals("No se encontró la sucursal con ID: " + sucursalId, ex.getMessage());
      verify(sucursalService, times(1)).findById(sucursalId);
    }
  }
}
