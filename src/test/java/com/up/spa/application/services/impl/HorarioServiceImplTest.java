package com.up.spa.application.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.up.spa.application.model.Horario;
import com.up.spa.application.repository.HorarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



@ExtendWith(MockitoExtension.class)
class HorarioServiceImplTest {

  @Mock
  private HorarioRepository horarioRepository;

  @InjectMocks
  private HorarioServiceImpl horarioService;

  private Horario horario1;
  private Horario horario2;

  @BeforeEach
  void setUp() {
    // Inicializar Horarios de prueba
    horario1 = Horario.builder()
        .diaSemana("Lunes")
        .horaInicio("09:00")
        .horaFin("17:00")
        .build();

    horario2 = Horario.builder()
        .diaSemana("Martes")
        .horaInicio("10:00")
        .horaFin("18:00")
        .build();
  }

  // Test para obtenerHorariosPorProfesional cuando el profesional tiene horarios
  @Test
  void testObtenerHorariosPorProfesional_ReturnsHorarios() {
    // Arrange
    String profesionalId = "64df18b9c44e9c2c4fd1a555";
    when(horarioRepository.findHorariosByProfesionalId(profesionalId))
        .thenReturn(Arrays.asList(horario1, horario2));

    // Act
    List<Horario> result = horarioService.obtenerHorariosPorProfesional(profesionalId);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("Lunes", result.get(0).getDiaSemana());
    assertEquals("Martes", result.get(1).getDiaSemana());
    verify(horarioRepository, times(1)).findHorariosByProfesionalId(profesionalId);
  }

  // Test para obtenerHorariosPorProfesional cuando el profesional no tiene horarios
  @Test
  void testObtenerHorariosPorProfesional_ReturnsEmptyList() {
    // Arrange
    String profesionalId = "64df18b9c44e9c2c4fd1a666";
    when(horarioRepository.findHorariosByProfesionalId(profesionalId))
        .thenReturn(Collections.emptyList());

    // Act
    List<Horario> result = horarioService.obtenerHorariosPorProfesional(profesionalId);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(horarioRepository, times(1)).findHorariosByProfesionalId(profesionalId);
  }

  // Test para calcularHoraFin con duración que no cruza medianoche
  @Test
  void testCalcularHoraFin_NormalDuration() {
    // Arrange
    String horaInicio = "09:00";
    int duracionMinutos = 120; // 2 horas

    // Act
    String horaFin = horarioService.calcularHoraFin(horaInicio, duracionMinutos);

    // Assert
    assertEquals("11:00", horaFin);
  }

  // Test para calcularHoraFin con duración que cruza medianoche, algo que nunca va a pasar en un spa jaja
  @Test
  void testCalcularHoraFin_CrossesMidnight() {
    // Arrange
    String horaInicio = "23:30";
    int duracionMinutos = 90; // 1 hora y 30 minutos

    // Act
    String horaFin = horarioService.calcularHoraFin(horaInicio, duracionMinutos);

    // Assert
    assertEquals("01:00", horaFin); // 23:30 + 1:30 = 01:00 del día siguiente
  }

  // Test para calcularHoraFin con formato de hora inválido
  @Test
  void testCalcularHoraFin_InvalidHoraInicio() {
    // Arrange
    String horaInicio = "25:00"; // Hora inválida
    int duracionMinutos = 60;

    // Act & Assert
    assertThrows(DateTimeParseException.class, () -> {
      horarioService.calcularHoraFin(horaInicio, duracionMinutos);
    });
  }

  // Test para obtenerDiaSemana con fecha válida
  @Test
  void testObtenerDiaSemana_ValidDate() {
    // Arrange
    String fecha = "2024-12-02"; // Sábado

    // Act
    String diaSemana = horarioService.obtenerDiaSemana(fecha);

    // Assert
    assertEquals("lunes", diaSemana.toLowerCase());
  }

  // Test para obtenerDiaSemana con fecha inválida
  @Test
  void testObtenerDiaSemana_InvalidDate() {
    // Arrange
    String fecha = "2024-13-01"; // Mes inválido

    // Act & Assert
    assertThrows(DateTimeParseException.class, () -> {
      horarioService.obtenerDiaSemana(fecha);
    });
  }
}
