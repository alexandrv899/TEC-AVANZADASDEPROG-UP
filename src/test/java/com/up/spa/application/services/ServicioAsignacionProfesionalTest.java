package com.up.spa.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.up.spa.application.exception.ResourceNotFoundException;
import com.up.spa.application.exception.TurnoNoDisponibleException;
import com.up.spa.application.model.Profesional;
import com.up.spa.application.model.Servicio;
import com.up.spa.application.model.Sucursal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServicioAsignacionProfesionalTest {

  @Mock
  private ISucursalService sucursalService;

  @Mock
  private DisponibilidadValidatorService disponibilidadValidator;

  @InjectMocks
  private ServicioAsignacionProfesional servicioAsignacionProfesional;

  @BeforeEach
  void setUp() {
    // Inicialización si es necesaria
  }

  // Test para obtenerProfesionalGenerico - Sucursal no encontrada
  @Test
  void obtenerProfesionalGenerico_SucursalNoEncontrada_LanzaResourceNotFoundException() {
    // Arrange
    String sucursalId = "sucursal123";
    String servicioId = "servicio456";
    String fechaStr = "2024-12-01";
    String horaInicio = "10:00";
    String horaFin = "11:00";

    when(sucursalService.findById(sucursalId)).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> servicioAsignacionProfesional.obtenerProfesionalGenerico(sucursalId, servicioId, fechaStr, horaInicio, horaFin)
    );

    assertEquals("Sucursal no encontrada", exception.getMessage());

    verify(sucursalService, times(1)).findById(sucursalId);
    verifyNoMoreInteractions(sucursalService);
    verifyNoInteractions(disponibilidadValidator);
  }

  // Test para obtenerProfesionalGenerico - Servicio no encontrado en la sucursal
  @Test
  void obtenerProfesionalGenerico_ServicioNoEncontrado_LanzaResourceNotFoundException() {
    // Arrange
    String sucursalId = "sucursal123";
    String servicioId = "servicio456";
    String fechaStr = "2024-12-01";
    String horaInicio = "10:00";
    String horaFin = "11:00";

    Servicio servicioExistente = new Servicio();
    servicioExistente.setId("servicio789"); // Diferente al buscado

    Profesional profesional = new Profesional();
    profesional.setId("profesional123");

    Sucursal sucursal = new Sucursal();
    sucursal.setId(sucursalId);
    sucursal.setServicios(Arrays.asList(servicioExistente));
    sucursal.setProfesionales(Arrays.asList(profesional));

    when(sucursalService.findById(sucursalId)).thenReturn(Optional.of(sucursal));

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> servicioAsignacionProfesional.obtenerProfesionalGenerico(sucursalId, servicioId, fechaStr, horaInicio, horaFin)
    );

    assertEquals("Servicio no encontrado en la sucursal", exception.getMessage());

    verify(sucursalService, times(1)).findById(sucursalId);
    verifyNoMoreInteractions(sucursalService);
    verifyNoInteractions(disponibilidadValidator);
  }

  // Test para obtenerProfesionalGenerico - No hay profesionales en la sucursal
  @Test
  void obtenerProfesionalGenerico_NoHayProfesionales_LanzaTurnoNoDisponibleException() {
    // Arrange
    String sucursalId = "sucursal123";
    String servicioId = "servicio456";
    String fechaStr = "2024-12-01";
    String horaInicio = "10:00";
    String horaFin = "11:00";

    Servicio servicio = new Servicio();
    servicio.setId(servicioId);

    Sucursal sucursal = new Sucursal();
    sucursal.setId(sucursalId);
    sucursal.setServicios(Arrays.asList(servicio));
    sucursal.setProfesionales(Collections.emptyList()); // Sin profesionales

    when(sucursalService.findById(sucursalId)).thenReturn(Optional.of(sucursal));

    // Act & Assert
    TurnoNoDisponibleException exception = assertThrows(
        TurnoNoDisponibleException.class,
        () -> servicioAsignacionProfesional.obtenerProfesionalGenerico(sucursalId, servicioId, fechaStr, horaInicio, horaFin)
    );

    assertEquals("No hay profesionales disponibles en el horario solicitado.", exception.getMessage());

    verify(sucursalService, times(1)).findById(sucursalId);
    verifyNoMoreInteractions(sucursalService);
    verifyNoInteractions(disponibilidadValidator);
  }

  // Test para obtenerProfesionalGenerico - Ningún profesional puede realizar el servicio
  @Test
  void obtenerProfesionalGenerico_NingunProfesionalPuedeRealizarServicio_LanzaTurnoNoDisponibleException() {
    // Arrange
    String sucursalId = "sucursal123";
    String servicioId = "servicio456";
    String fechaStr = "2024-12-01";
    String horaInicio = "10:00";
    String horaFin = "11:00";

    Servicio servicio = new Servicio();
    servicio.setId(servicioId);

    Profesional profesional1 = new Profesional();
    profesional1.setId("profesional123");

    Profesional profesional2 = new Profesional();
    profesional2.setId("profesional456");

    Sucursal sucursal = new Sucursal();
    sucursal.setId(sucursalId);
    sucursal.setServicios(Arrays.asList(servicio));
    sucursal.setProfesionales(Arrays.asList(profesional1, profesional2));

    when(sucursalService.findById(sucursalId)).thenReturn(Optional.of(sucursal));

    // Configurar DisponibilidadValidatorService para que ninguno pueda realizar el servicio
    when(disponibilidadValidator.puedeRealizarServicio(profesional1, servicio)).thenReturn(false);
    when(disponibilidadValidator.puedeRealizarServicio(profesional2, servicio)).thenReturn(false);

    // Act & Assert
    TurnoNoDisponibleException exception = assertThrows(
        TurnoNoDisponibleException.class,
        () -> servicioAsignacionProfesional.obtenerProfesionalGenerico(sucursalId, servicioId, fechaStr, horaInicio, horaFin)
    );

    assertEquals("No hay profesionales disponibles en el horario solicitado.", exception.getMessage());

    verify(sucursalService, times(1)).findById(sucursalId);
    verify(disponibilidadValidator, times(1)).puedeRealizarServicio(profesional1, servicio);
    verify(disponibilidadValidator, times(1)).puedeRealizarServicio(profesional2, servicio);
    verifyNoMoreInteractions(sucursalService);
  }

}


