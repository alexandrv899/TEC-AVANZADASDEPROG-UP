package com.up.spa.application.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.up.spa.application.model.Servicio;
import com.up.spa.application.repository.ServicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
class ServicioServiceImplTest {

  @Mock
  private ServicioRepository servicioRepository;

  @InjectMocks
  private ServicioServiceImpl servicioService;

  private Servicio servicio;

  @BeforeEach
  void setUp() {
    // Inicializar un Servicio de prueba utilizando el patrón Builder de Lombok
    servicio = Servicio.builder()
        .id("64df18b9c44e9c2c4fd1a111")
        .nombre("Servicio Test")
        .duracion(60)
        .especialidadId("64df18b9c44e9c2c4fd1a222")
        .build();
  }

  // Test para findServicioById cuando el servicio existe
  @Test
  void testFindServicioById_ServicioExiste() {
    // Arrange
    String servicioId = "64df18b9c44e9c2c4fd1a111";
    when(servicioRepository.findServicioById(servicioId)).thenReturn(servicio);

    // Act
    Servicio resultado = servicioService.findServicioById(servicioId);

    // Assert
    assertNotNull(resultado, "El servicio debería no ser nulo");
    assertEquals(servicioId, resultado.getId(), "El ID del servicio debería coincidir");
    assertEquals("Servicio Test", resultado.getNombre(), "El nombre del servicio debería coincidir");
    assertEquals(60, resultado.getDuracion(), "La duración del servicio debería coincidir");
    assertEquals("64df18b9c44e9c2c4fd1a222", resultado.getEspecialidadId(), "El ID de especialidad debería coincidir");
    verify(servicioRepository, times(1)).findServicioById(servicioId);
  }

  // Test para findServicioById cuando el servicio no existe
  @Test
  void testFindServicioById_ServicioNoExiste() {
    // Arrange
    String servicioId = "nonExistentId";
    when(servicioRepository.findServicioById(servicioId)).thenReturn(null);

    // Act
    Servicio resultado = servicioService.findServicioById(servicioId);

    // Assert
    assertNull(resultado, "El servicio debería ser nulo");
    verify(servicioRepository, times(1)).findServicioById(servicioId);
  }

  // Test para obtenerDuracionServicio cuando el servicio existe
  @Test
  void testObtenerDuracionServicio_ServicioExiste() {
    // Arrange
    String servicioId = "64df18b9c44e9c2c4fd1a111";
    when(servicioRepository.findServicioById(servicioId)).thenReturn(servicio);

    // Act
    int duracion = servicioService.obtenerDuracionServicio(servicioId);

    // Assert
    assertEquals(60, duracion, "La duración debería ser 60 minutos");
    verify(servicioRepository, times(1)).findServicioById(servicioId);
  }

  // Test para obtenerDuracionServicio cuando el servicio no existe
  @Test
  void testObtenerDuracionServicio_ServicioNoExiste() {
    // Arrange
    String servicioId = "nonExistentId";
    when(servicioRepository.findServicioById(servicioId)).thenReturn(null);

    // Act
    int duracion = servicioService.obtenerDuracionServicio(servicioId);

    // Assert
    assertEquals(0, duracion, "La duración debería ser 0 minutos cuando el servicio no existe");
    verify(servicioRepository, times(1)).findServicioById(servicioId);
  }
}
