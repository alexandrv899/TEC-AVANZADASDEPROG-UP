package com.up.spa.application.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.up.spa.application.model.Servicio;
import com.up.spa.application.model.Sucursal;
import com.up.spa.application.repository.SucursalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;



@ExtendWith(MockitoExtension.class)
class SucursalServiceImplTest {

  @Mock
  private SucursalRepository sucursalRepository;

  @InjectMocks
  private SucursalServiceImpl sucursalService;

  private Sucursal sucursal1;
  private Sucursal sucursal2;
  private Servicio servicio1;
  private Servicio servicio2;

  @BeforeEach
  void setUp() {

    servicio1 = Servicio.builder()
        .id("64df18b9c44e9c2c4fd1a111")
        .nombre("Servicio 1")
        .duracion(60)
        .especialidadId("64df18b9c44e9c2c4fd1a222")
        .build();

    servicio2 = Servicio.builder()
        .id("64df18b9c44e9c2c4fd1a333")
        .nombre("Servicio 2")
        .duracion(30)
        .especialidadId("64df18b9c44e9c2c4fd1a444")
        .build();


    sucursal1 = Sucursal.builder()
        .id("64df18b9c44e9c2c4fd1b111")
        .nombre("Sucursal 1")
        .direccion("Direccion 1")
        .servicios(Arrays.asList(servicio1, servicio2))
        .build();

    sucursal2 = Sucursal.builder()
        .id("64df18b9c44e9c2c4fd1b222")
        .nombre("Sucursal 2")
        .direccion("Direccion 2")
        .servicios(Collections.emptyList())
        .build();
  }


  @Test
  void testGetAllSucursales_ReturnsListOfSucursales() {
    // Arrange
    List<Sucursal> sucursales = Arrays.asList(sucursal1, sucursal2);
    when(sucursalRepository.findAllSucursales()).thenReturn(sucursales);

    // Act
    List<Sucursal> result = sucursalService.getAllSucursales();

    // Assert
    assertNotNull(result, "La lista de sucursales no debería ser nula");
    assertEquals(2, result.size(), "Debería devolver 2 sucursales");
    assertTrue(result.contains(sucursal1), "La lista debería contener sucursal1");
    assertTrue(result.contains(sucursal2), "La lista debería contener sucursal2");
    verify(sucursalRepository, times(1)).findAllSucursales();
  }


  @Test
  void testGetAllSucursales_ReturnsEmptyList() {
    // Arrange
    when(sucursalRepository.findAllSucursales()).thenReturn(Collections.emptyList());

    // Act
    List<Sucursal> result = sucursalService.getAllSucursales();

    // Assert
    assertNotNull(result, "La lista de sucursales no debería ser nula");
    assertTrue(result.isEmpty(), "La lista de sucursales debería estar vacía");
    verify(sucursalRepository, times(1)).findAllSucursales();
  }

  @Test
  void testFindById_SucursalExiste() {
    // Arrange
    String sucursalId = "64df18b9c44e9c2c4fd1b111";
    when(sucursalRepository.findById(sucursalId)).thenReturn(Optional.of(sucursal1));

    // Act
    Optional<Sucursal> result = sucursalService.findById(sucursalId);

    // Assert
    assertTrue(result.isPresent(), "La sucursal debería estar presente");
    assertEquals(sucursal1, result.get(), "La sucursal devuelta debería ser sucursal1");
    verify(sucursalRepository, times(1)).findById(sucursalId);
  }


  @Test
  void testFindById_SucursalNoExiste() {
    // Arrange
    String sucursalId = "nonExistentId";
    when(sucursalRepository.findById(sucursalId)).thenReturn(Optional.empty());

    // Act
    Optional<Sucursal> result = sucursalService.findById(sucursalId);

    // Assert
    assertFalse(result.isPresent(), "La sucursal no debería estar presente");
    verify(sucursalRepository, times(1)).findById(sucursalId);
  }

  // Test para obtenerServicioDeSucursal cuando el servicio existe en la sucursal
  @Test
  void testObtenerServicioDeSucursal_ServicioExiste() {
    // Arrange
    String serviceId = "64df18b9c44e9c2c4fd1a111";
    Sucursal sucursal = sucursal1; // Sucursal que contiene servicio1 y servicio2

    // Act
    Optional<Servicio> result = sucursalService.obtenerServicioDeSucursal(serviceId, sucursal);

    // Assert
    assertTrue(result.isPresent(), "El servicio debería estar presente en la sucursal");
    assertEquals(servicio1, result.get(), "El servicio devuelto debería ser servicio1");
  }

  // Test para obtenerServicioDeSucursal cuando el servicio no existe en la sucursal
  @Test
  void testObtenerServicioDeSucursal_ServicioNoExiste() {
    // Arrange
    String serviceId = "nonExistentServiceId";
    Sucursal sucursal = sucursal1; // Sucursal que contiene servicio1 y servicio2

    // Act
    Optional<Servicio> result = sucursalService.obtenerServicioDeSucursal(serviceId, sucursal);

    // Assert
    assertFalse(result.isPresent(), "El servicio no debería estar presente en la sucursal");
  }

  // Test para obtenerServicioDeSucursal cuando la sucursal no tiene servicios
  @Test
  void testObtenerServicioDeSucursal_SucursalSinServicios() {
    // Arrange
    String serviceId = "64df18b9c44e9c2c4fd1a111";
    Sucursal sucursal = sucursal2; // Sucursal que no tiene servicios

    // Act
    Optional<Servicio> result = sucursalService.obtenerServicioDeSucursal(serviceId, sucursal);

    // Assert
    assertFalse(result.isPresent(), "La sucursal no tiene servicios, por lo que el servicio no debería estar presente");
  }
}
