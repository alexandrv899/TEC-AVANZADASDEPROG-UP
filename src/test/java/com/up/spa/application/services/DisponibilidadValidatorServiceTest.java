package com.up.spa.application.services;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.up.spa.application.exception.TurnoNoDisponibleException;
import com.up.spa.application.model.Cliente;
import com.up.spa.application.model.Horario;
import com.up.spa.application.model.Profesional;
import com.up.spa.application.model.Servicio;
import com.up.spa.application.repository.TurnoRepository;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DisponibilidadValidatorServiceTest {

  @Mock
  private TurnoRepository turnoRepository;

  @Mock
  private IHorarioService horarioService;

  @InjectMocks
  private DisponibilidadValidatorService disponibilidadValidatorService;

  @BeforeEach
  void setUp() {
    // Inicialización si es necesaria
  }

  // Test para validarDisponibilidadCliente - Cliente ocupado
  @Test
  void validarDisponibilidadCliente_ClienteOcupado_LanzaExcepcion() {
    // Arrange
    Cliente cliente = new Cliente();
    cliente.setId("cliente123");
    String fecha = "2024-12-01";
    String horaInicio = "10:00";
    String horaFin = "11:00";

    when(turnoRepository.isClienteOcupado(cliente.getId(), fecha, horaInicio, horaFin))
        .thenReturn(true);

    // Act & Assert
    TurnoNoDisponibleException exception = assertThrows(
        TurnoNoDisponibleException.class,
        () -> disponibilidadValidatorService.validarDisponibilidadCliente(cliente, fecha, horaInicio, horaFin)
    );

    assertEquals("El cliente ya tiene un turno en el mismo día y horario.", exception.getMessage());

    verify(turnoRepository, times(1)).isClienteOcupado(cliente.getId(), fecha, horaInicio, horaFin);
  }

  // Test para validarDisponibilidadCliente - Cliente no ocupado
  @Test
  void validarDisponibilidadCliente_ClienteDisponible_NoLanzaExcepcion() {
    // Arrange
    Cliente cliente = new Cliente();
    cliente.setId("cliente456");
    String fecha = "2024-12-02";
    String horaInicio = "14:00";
    String horaFin = "15:00";

    when(turnoRepository.isClienteOcupado(cliente.getId(), fecha, horaInicio, horaFin))
        .thenReturn(false);

    // Act & Assert
    assertDoesNotThrow(() ->
        disponibilidadValidatorService.validarDisponibilidadCliente(cliente, fecha, horaInicio, horaFin)
    );

    verify(turnoRepository, times(1)).isClienteOcupado(cliente.getId(), fecha, horaInicio, horaFin);
  }

  // Test para puedeRealizarServicio - Profesional puede realizar servicio
  @Test
  void puedeRealizarServicio_ProfesionalTieneEspecialidad_RetornaTrue() {
    // Arrange
    Profesional profesional = new Profesional();
    profesional.setId("profesional123");
    profesional.setEspecialidadIds(Arrays.asList("esp1", "esp2", "esp3"));

    Servicio servicio = new Servicio();
    servicio.setId("servicio456");
    servicio.setEspecialidadId("esp2");

    // Act
    boolean puedeRealizar = disponibilidadValidatorService.puedeRealizarServicio(profesional, servicio);

    // Assert
    assertTrue(puedeRealizar, "El profesional debería poder realizar el servicio.");
  }

  // Test para puedeRealizarServicio - Profesional no puede realizar servicio
  @Test
  void puedeRealizarServicio_ProfesionalNoTieneEspecialidad_RetornaFalse() {
    // Arrange
    Profesional profesional = new Profesional();
    profesional.setId("profesional789");
    profesional.setEspecialidadIds(Arrays.asList("esp4", "esp5"));

    Servicio servicio = new Servicio();
    servicio.setId("servicio101");
    servicio.setEspecialidadId("esp2");

    // Act
    boolean puedeRealizar = disponibilidadValidatorService.puedeRealizarServicio(profesional, servicio);

    // Assert
    assertFalse(puedeRealizar, "El profesional no debería poder realizar el servicio.");
  }

  // Test para validarDisponibilidadProfesional - Horario válido y no ocupado
  @Test
  void validarDisponibilidadProfesional_HorarioValido_NoOcupado_RetornaTrue() {
    // Arrange
    Profesional profesional = new Profesional();
    profesional.setId("profesional123");

    String fecha = "2024-12-03";
    String horaInicio = "09:00";
    String horaFin = "10:00";

    String diaSemana = "Tuesday";

    // Configurar el servicio de horarios
    Horario horario = new Horario();
    horario.setDiaSemana("Tuesday");
    horario.setHoraInicio("08:00");
    horario.setHoraFin("12:00");

    when(horarioService.obtenerDiaSemana(fecha)).thenReturn(diaSemana);
    when(horarioService.obtenerHorariosPorProfesional(profesional.getId()))
        .thenReturn(Collections.singletonList(horario));

    // Configurar el turnoRepository para que el profesional no esté ocupado
    when(turnoRepository.isProfesionalOcupado(profesional.getId(), fecha, horaInicio, horaFin))
        .thenReturn(false);

    // Act
    boolean estaDisponible = disponibilidadValidatorService.validarDisponibilidadProfesional(
        profesional, fecha, horaInicio, horaFin
    );

    // Assert
    assertTrue(estaDisponible, "El profesional debería estar disponible para el turno.");

    verify(horarioService, times(1)).obtenerDiaSemana(fecha);
    verify(horarioService, times(1)).obtenerHorariosPorProfesional(profesional.getId());
    verify(turnoRepository, times(1)).isProfesionalOcupado(profesional.getId(), fecha, horaInicio, horaFin);
  }

  // Test para validarDisponibilidadProfesional - Horario inválido
  @Test
  void validarDisponibilidadProfesional_HorarioInvalido_RetornaFalse() {
    // Arrange
    Profesional profesional = new Profesional();
    profesional.setId("profesional456");

    String fecha = "2024-12-04";
    String horaInicio = "13:00";
    String horaFin = "14:00";

    String diaSemana = "Wednesday";

    // Configurar el servicio de horarios
    Horario horario = new Horario();
    horario.setDiaSemana("Wednesday");
    horario.setHoraInicio("08:00");
    horario.setHoraFin("12:00"); // El horario laboral termina a las 12:00

    when(horarioService.obtenerDiaSemana(fecha)).thenReturn(diaSemana);
    when(horarioService.obtenerHorariosPorProfesional(profesional.getId()))
        .thenReturn(Collections.singletonList(horario));

    // Act
    boolean estaDisponible = disponibilidadValidatorService.validarDisponibilidadProfesional(
        profesional, fecha, horaInicio, horaFin
    );

    // Assert
    assertFalse(estaDisponible, "El profesional no debería estar disponible fuera de su horario laboral.");

    verify(horarioService, times(1)).obtenerDiaSemana(fecha);
    verify(horarioService, times(1)).obtenerHorariosPorProfesional(profesional.getId());
    verify(turnoRepository, times(0)).isProfesionalOcupado(anyString(), anyString(), anyString(), anyString());
  }

  // Test para validarDisponibilidadProfesional - Horario válido pero ocupado
  @Test
  void validarDisponibilidadProfesional_HorarioValido_Ocupado_RetornaFalse() {
    // Arrange
    Profesional profesional = new Profesional();
    profesional.setId("profesional789");

    String fecha = "2024-12-05";
    String horaInicio = "10:00";
    String horaFin = "11:00";

    String diaSemana = "Thursday";

    // Configurar el servicio de horarios
    Horario horario = new Horario();
    horario.setDiaSemana("Thursday");
    horario.setHoraInicio("09:00");
    horario.setHoraFin("17:00");

    when(horarioService.obtenerDiaSemana(fecha)).thenReturn(diaSemana);
    when(horarioService.obtenerHorariosPorProfesional(profesional.getId()))
        .thenReturn(Collections.singletonList(horario));

    // Configurar el turnoRepository para que el profesional esté ocupado
    when(turnoRepository.isProfesionalOcupado(profesional.getId(), fecha, horaInicio, horaFin))
        .thenReturn(true);

    // Act
    boolean estaDisponible = disponibilidadValidatorService.validarDisponibilidadProfesional(
        profesional, fecha, horaInicio, horaFin
    );

    // Assert
    assertFalse(estaDisponible, "El profesional no debería estar disponible porque está ocupado.");

    verify(horarioService, times(1)).obtenerDiaSemana(fecha);
    verify(horarioService, times(1)).obtenerHorariosPorProfesional(profesional.getId());
    verify(turnoRepository, times(1)).isProfesionalOcupado(profesional.getId(), fecha, horaInicio, horaFin);
  }
}
