package com.up.spa.application.component;

import static org.junit.jupiter.api.Assertions.*;

import com.up.spa.application.exception.InvalidTurnoDataException;
import com.up.spa.application.exception.ResourceNotFoundException;
import com.up.spa.application.model.*;
import com.up.spa.application.model.dto.request.TurnoDTO;
import java.util.Calendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

class TurnoValidatorTest {

  private TurnoValidator turnoValidator;
  private Sucursal sucursal;
  private TurnoDTO turnoDto;


  @BeforeEach
  void setUp() {
    turnoValidator = new TurnoValidator();

    // Inicializar Sucursal de prueba
    sucursal = Sucursal.builder()
        .id("sucursal1")
        .nombre("Sucursal Central")
        .direccion("Calle Falsa 123")
        .servicios(Arrays.asList(
            Servicio.builder().id("servicio1").nombre("Corte de Pelo").build(),
            Servicio.builder().id("servicio2").nombre("Coloración").build()
        ))
        .profesionales(Arrays.asList(
            Profesional.builder().id("profesional1").nombre("Juan Perez").build(),
            Profesional.builder().id("profesional2").nombre("Maria Lopez").build()
        ))
        .build();

    // Inicializar TurnoDTO de prueba
    turnoDto = TurnoDTO.builder()
        .id(null)
        .sucursalId("sucursal1")
        .email("cliente@example.com")
        .servicioId("servicio1")
        .fecha("2025-12-31")
        .hora("15:30")
        .profesionalId("profesional1") // Opcional, puede ser null
        .build();
  }

  // ### 4.1. `validateTurnoData` Tests ###

  /**
   * Test para verificar que la validación es exitosa cuando todos los datos son válidos.
   */
  @Test
  void testValidateTurnoData_Success_AllValid() {
    // Act & Assert
    assertDoesNotThrow(() -> {
      turnoValidator.validateTurnoData(turnoDto, sucursal);
    });
  }

  /**
   * Test para verificar que se lanza ResourceNotFoundException cuando la sucursal es null.
   */
  @Test
  void testValidateTurnoData_SucursalNull() {
    // Arrange
    TurnoDTO dto = TurnoDTO.builder()
        .id(null)
        .sucursalId("sucursal1")
        .email("cliente@example.com")
        .servicioId("servicio1")
        .fecha("2025-12-31")
        .hora("15:30")
        .profesionalId("profesional1")
        .build();

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      turnoValidator.validateTurnoData(dto, null);
    }, "Se debería lanzar una ResourceNotFoundException cuando la sucursal es null.");

    assertEquals("No se encontró la sucursal con ID: sucursal1", exception.getMessage());
  }

  /**
   * Test para verificar que se lanza InvalidTurnoDataException cuando el servicio no existe en la sucursal.
   */
  @Test
  void testValidateTurnoData_ServicioNoExiste() {
    // Arrange
    TurnoDTO dto = TurnoDTO.builder()
        .id(null)
        .sucursalId("sucursal1")
        .email("cliente@example.com")
        .servicioId("servicioInvalido")
        .fecha("2025-12-31")
        .hora("15:30")
        .profesionalId("profesional1")
        .build();

    // Act & Assert
    InvalidTurnoDataException exception = assertThrows(InvalidTurnoDataException.class, () -> {
      turnoValidator.validateTurnoData(dto, sucursal);
    }, "Se debería lanzar una InvalidTurnoDataException cuando el servicio no existe en la sucursal.");

    assertEquals("El servicio con ID servicioInvalido no está disponible en la sucursal Sucursal Central", exception.getMessage());
  }

  /**
   * Test para verificar que se lanza InvalidTurnoDataException cuando se especifica un profesional que no pertenece a la sucursal.
   */
  @Test
  void testValidateTurnoData_ProfesionalNoExiste() {
    // Arrange
    TurnoDTO dto = TurnoDTO.builder()
        .id(null)
        .sucursalId("sucursal1")
        .email("cliente@example.com")
        .servicioId("servicio1")
        .fecha("2025-12-31")
        .hora("15:30")
        .profesionalId("profesionalInvalido") // Profesional que no existe
        .build();

    // Act & Assert
    InvalidTurnoDataException exception = assertThrows(InvalidTurnoDataException.class, () -> {
      turnoValidator.validateTurnoData(dto, sucursal);
    }, "Se debería lanzar una InvalidTurnoDataException cuando el profesional no pertenece a la sucursal.");

    assertEquals("El profesional con ID profesionalInvalido no pertenece a la sucursal Sucursal Central", exception.getMessage());
  }

  /**
   * Test para verificar que la validación pasa cuando el profesional no está especificado (es opcional).
   */
  @Test
  void testValidateTurnoData_NoProfesionalEspecificado() {
    // Arrange
    TurnoDTO dto = TurnoDTO.builder()
        .id(null)
        .sucursalId("sucursal1")
        .email("cliente@example.com")
        .servicioId("servicio1")
        .fecha("2025-12-31")
        .hora("15:30")
        .profesionalId(null) // Profesional no especificado
        .build();

    // Act & Assert
    assertDoesNotThrow(() -> {
      turnoValidator.validateTurnoData(dto, sucursal);
    });
  }

  /**
   * Test para verificar que se lanza InvalidTurnoDataException cuando la fecha tiene un formato inválido.
   */
  @Test
  void testValidateTurnoData_FechayHoraFormatoInvalido() {
    // Arrange
    TurnoDTO dto = TurnoDTO.builder()
        .id(null)
        .sucursalId("sucursal1")
        .email("cliente@example.com")
        .servicioId("servicio1")
        .fecha("31-12-2025") // Formato inválido
        .hora("15:30")
        .profesionalId("profesional1")
        .build();

    // Act & Assert
    InvalidTurnoDataException exception = assertThrows(InvalidTurnoDataException.class, () -> {
      turnoValidator.validateTurnoData(dto, sucursal);
    }, "Se debería lanzar una InvalidTurnoDataException cuando el formato de fecha es inválido.");

    assertEquals("El formato de la fecha es inválido. Se espera yyyy-MM-dd.", exception.getMessage());
  }

  /**
   * Test para verificar que se lanza InvalidTurnoDataException cuando la fecha y hora están en el pasado.
   */
  @Test
  void testValidateTurnoData_FechayHoraEnElPasado() {
    // Arrange
    // Obtener la fecha y hora actual menos una hora
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.HOUR, -1);
    Date fechaPasada = cal.getTime();
    String fecha = new SimpleDateFormat("yyyy-MM-dd").format(fechaPasada);
    String hora = new SimpleDateFormat("HH:mm").format(fechaPasada);

    TurnoDTO dto = TurnoDTO.builder()
        .id(null)
        .sucursalId("sucursal1")
        .email("cliente@example.com")
        .servicioId("servicio1")
        .fecha(fecha)
        .hora(hora)
        .profesionalId("profesional1")
        .build();

    // Act & Assert
    InvalidTurnoDataException exception = assertThrows(InvalidTurnoDataException.class, () -> {
      turnoValidator.validateTurnoData(dto, sucursal);
    }, "Se debería lanzar una InvalidTurnoDataException cuando la fecha y hora están en el pasado.");

    assertEquals("La fecha y hora seleccionadas son anteriores a la actual.", exception.getMessage());
  }

  /**
   * Test para verificar que se lanza InvalidTurnoDataException cuando la hora tiene un formato inválido.
   */
  @Test
  void testValidateTurnoData_HoraFormatoInvalido() {
    // Arrange
    TurnoDTO dto = TurnoDTO.builder()
        .id(null)
        .sucursalId("sucursal1")
        .email("cliente@example.com")
        .servicioId("servicio1")
        .fecha("2025-12-31")
        .hora("3 PM") // Formato inválido
        .profesionalId("profesional1")
        .build();

    // Act & Assert
    InvalidTurnoDataException exception = assertThrows(InvalidTurnoDataException.class, () -> {
      turnoValidator.validateTurnoData(dto, sucursal);
    }, "Se debería lanzar una InvalidTurnoDataException cuando la hora tiene un formato inválido.");

    assertEquals("El formato de la hora es inválido. Se espera HH:mm.", exception.getMessage());
  }

  /**
   * Test para verificar que se lanza InvalidTurnoDataException cuando solo la fecha es inválida, pero la hora es válida.
   */
  @Test
  void testValidateTurnoData_FechayHoraSoloFechaInvalida() {
    // Arrange
    TurnoDTO dto = TurnoDTO.builder()
        .id(null)
        .sucursalId("sucursal1")
        .email("cliente@example.com")
        .servicioId("servicio1")
        .fecha("2025/12/31") // Formato inválido
        .hora("15:30")
        .profesionalId("profesional1")
        .build();

    // Act & Assert
    InvalidTurnoDataException exception = assertThrows(InvalidTurnoDataException.class, () -> {
      turnoValidator.validateTurnoData(dto, sucursal);
    }, "Se debería lanzar una InvalidTurnoDataException cuando solo la fecha es inválida.");

    assertEquals("El formato de la fecha es inválido. Se espera yyyy-MM-dd.", exception.getMessage());
  }

  /**
   * Test para verificar que se lanza InvalidTurnoDataException cuando ambos, fecha y hora, son inválidos.
   */
  @Test
  void testValidateTurnoData_FechayHoraTotalmenteInvalida() {
    // Arrange
    TurnoDTO dto = TurnoDTO.builder()
        .id(null)
        .sucursalId("sucursal1")
        .email("cliente@example.com")
        .servicioId("servicio1")
        .fecha("31-12-2025") // Formato inválido
        .hora("invalidTime")   // Formato inválido
        .profesionalId("profesional1")
        .build();

    // Act & Assert
    InvalidTurnoDataException exception = assertThrows(InvalidTurnoDataException.class, () -> {
      turnoValidator.validateTurnoData(dto, sucursal);
    }, "Se debería lanzar una InvalidTurnoDataException cuando ambos, fecha y hora, son inválidos.");

    // Dado que la fecha es validada primero, el mensaje de error será sobre la fecha.
    assertEquals("El formato de la fecha es inválido. Se espera yyyy-MM-dd.", exception.getMessage());
  }


  /**
   * Test para verificar que se lanza InvalidTurnoDataException cuando la hora es null.
   */
  @Test
  void testValidateTurnoData_HoraNull() {
    // Arrange
    TurnoDTO dto = TurnoDTO.builder()
        .id(null)
        .sucursalId("sucursal1")
        .email("cliente@example.com")
        .servicioId("servicio1")
        .fecha("2025-12-31")
        .hora(null) // Hora es null
        .profesionalId("profesional1")
        .build();

    // Act & Assert
    InvalidTurnoDataException exception = assertThrows(InvalidTurnoDataException.class, () -> {
      turnoValidator.validateTurnoData(dto, sucursal);
    }, "Se debería lanzar una InvalidTurnoDataException cuando la hora es null.");

    assertEquals("El formato de la hora es inválido. Se espera HH:mm.", exception.getMessage());
  }

  /**
   * Test para verificar que se lanza InvalidTurnoDataException cuando la fecha es null.
   */
  @Test
  void testValidateTurnoData_FechaNull() {
    // Arrange
    TurnoDTO dto = TurnoDTO.builder()
        .id(null)
        .sucursalId("sucursal1")
        .email("cliente@example.com")
        .servicioId("servicio1")
        .fecha(null) // Fecha es null
        .hora("15:30")
        .profesionalId("profesional1")
        .build();

    // Act & Assert
    InvalidTurnoDataException exception = assertThrows(InvalidTurnoDataException.class, () -> {
      turnoValidator.validateTurnoData(dto, sucursal);
    }, "Se debería lanzar una InvalidTurnoDataException cuando la fecha es null.");

    assertEquals("El formato de la fecha es inválido. Se espera yyyy-MM-dd.", exception.getMessage());
  }

  /**
   * Test para verificar que se lanza InvalidTurnoDataException cuando la fecha y hora tienen formato válido pero representan una fecha inexistente.
   */
  @Test
  void testValidateTurnoData_FechayHoraParsingError() {
    // Arrange
    TurnoDTO dto = TurnoDTO.builder()
        .id(null)
        .sucursalId("sucursal1")
        .email("cliente@example.com")
        .servicioId("servicio1")
        .fecha("2025-02-30") // Formato válido pero fecha inexistente
        .hora("15:30")         // Formato válido
        .profesionalId("profesional1")
        .build();

    // Act & Assert
    InvalidTurnoDataException exception = assertThrows(InvalidTurnoDataException.class, () -> {
      turnoValidator.validateTurnoData(dto, sucursal);
    }, "Se debería lanzar una InvalidTurnoDataException cuando la fecha y hora no se pueden parsear.");

    assertEquals("El formato de fecha y hora es inválido.", exception.getMessage());
  }

}
