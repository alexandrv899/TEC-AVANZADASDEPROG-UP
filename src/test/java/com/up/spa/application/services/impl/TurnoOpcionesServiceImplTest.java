package com.up.spa.application.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.up.spa.application.exception.ResourceNotFoundException;
import com.up.spa.application.exception.TurnoNoDisponibleException;
import com.up.spa.application.model.Horario;
import com.up.spa.application.model.Profesional;
import com.up.spa.application.model.Servicio;
import com.up.spa.application.model.Sucursal;
import com.up.spa.application.model.dto.request.TurnoOpcionesRequest;
import com.up.spa.application.model.dto.response.TurnoAgendaResponse;
import com.up.spa.application.repository.SucursalRepository;
import com.up.spa.application.repository.TurnoRepository;
import com.up.spa.application.services.IServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalTime;
import java.util.*;



@ExtendWith(MockitoExtension.class)
class TurnoOpcionesServiceImplTest {

  @Mock
  private SucursalRepository sucursalRepository;

  @Mock
  private TurnoRepository turnoRepository;

  @Mock
  private IServicio servicioService;

  @InjectMocks
  private TurnoOpcionesServiceImpl turnoOpcionesService;

  private Sucursal sucursal;
  private Servicio servicio;
  private Profesional profesional;
  private Horario horario;

  @BeforeEach
  void setUp() {
    // Inicializar Horario de prueba
    horario = Horario.builder()
        .diaSemana("Lunes")
        .horaInicio("09:00")
        .horaFin("17:00")
        .build();

    // Inicializar Profesional de prueba
    profesional = Profesional.builder()
        .id("profesional1")
        .nombre("Juan Perez")
        .especialidadIds(Arrays.asList("especialidad1"))
        .horarios(Arrays.asList(horario))
        .build();

    // Inicializar Sucursal de prueba
    sucursal = Sucursal.builder()
        .id("sucursal1")
        .nombre("Sucursal Central")
        .direccion("Calle Falsa 123")
        .profesionales(Arrays.asList(profesional))
        .servicios(Arrays.asList()) // Puede llenarse si es necesario
        .build();

    // Inicializar Servicio de prueba
    servicio = Servicio.builder()
        .id("servicio1")
        .nombre("Corte de Pelo")
        .duracion(60)
        .especialidadId("especialidad1")
        .build();
  }

  // Test para obtenerOpcionesTurnos cuando el servicio está disponible
  @Test
  void testObtenerOpcionesTurnos_ServicioDisponible() {
    // Arrange
    TurnoOpcionesRequest request = new TurnoOpcionesRequest(
        "sucursal1",
        "2024-04-29", // Supongamos que es lunes
        "servicio1"
    );

    when(sucursalRepository.findById("sucursal1")).thenReturn(Optional.of(sucursal));
    when(servicioService.findServicioById("servicio1")).thenReturn(servicio);
    when(turnoRepository.getIntervalosOcupados("profesional1", "2024-04-29")).thenReturn(Collections.emptyList());

    // Act
    List<TurnoAgendaResponse> result = turnoOpcionesService.obtenerOpcionesTurnos(request);

    // Assert
    assertNotNull(result, "El resultado no debería ser nulo");
    assertFalse(result.isEmpty(), "El resultado no debería estar vacío");

    TurnoAgendaResponse agenda = result.get(0);
    assertEquals("profesional1", agenda.getProfesionalId(), "El ID del profesional debería coincidir");
    assertEquals("Juan Perez", agenda.getNombreProfesional(), "El nombre del profesional debería coincidir");
    assertEquals("Corte de Pelo", agenda.getNombreServicio(), "El nombre del servicio debería coincidir");
    assertEquals(60, agenda.getDuracionServicio(), "La duración del servicio debería ser 60 minutos");
    assertFalse(agenda.getIntervalos().isEmpty(), "Los intervalos no deberían estar vacíos");

    // Verificar que el método del repositorio fue llamado correctamente
    verify(sucursalRepository, times(1)).findById("sucursal1");
    verify(servicioService, times(1)).findServicioById("servicio1");
    verify(turnoRepository, times(1)).getIntervalosOcupados("profesional1", "2024-04-29");
  }


  // Test para obtenerOpcionesTurnos cuando la sucursal no existe
  @Test
  void testObtenerOpcionesTurnos_SucursalNoExiste() {
    // Arrange
    TurnoOpcionesRequest request = new TurnoOpcionesRequest(
        "nonExistentSucursal",
        "2024-04-29",
        "servicio1"
    );

    when(sucursalRepository.findById("nonExistentSucursal")).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      turnoOpcionesService.obtenerOpcionesTurnos(request);
    }, "Se debería lanzar una ResourceNotFoundException");

    assertEquals("Sucursal no encontrada con ID: nonExistentSucursal", exception.getMessage());

    // Verificar que el método del repositorio fue llamado correctamente
    verify(sucursalRepository, times(1)).findById("nonExistentSucursal");
    verify(servicioService, never()).findServicioById(anyString());
    verify(turnoRepository, never()).getIntervalosOcupados(anyString(), anyString());
  }

  // Test para obtenerOpcionesTurnos cuando el servicio no existe
  @Test
  void testObtenerOpcionesTurnos_ServicioNoExiste() {
    // Arrange
    TurnoOpcionesRequest request = new TurnoOpcionesRequest(
        "sucursal1",
        "2024-04-29",
        "nonExistentServicio"
    );

    when(sucursalRepository.findById("sucursal1")).thenReturn(Optional.of(sucursal));
    when(servicioService.findServicioById("nonExistentServicio")).thenReturn(null);

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      turnoOpcionesService.obtenerOpcionesTurnos(request);
    }, "Se debería lanzar una ResourceNotFoundException");

    assertEquals("Servicio no encontrado con ID: nonExistentServicio", exception.getMessage());

    // Verificar que el método del repositorio fue llamado correctamente
    verify(sucursalRepository, times(1)).findById("sucursal1");
    verify(servicioService, times(1)).findServicioById("nonExistentServicio");
    verify(turnoRepository, never()).getIntervalosOcupados(anyString(), anyString());
  }
}
