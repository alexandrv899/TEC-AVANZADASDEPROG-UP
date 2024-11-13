package com.up.spa.application.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.up.spa.application.component.CancelacionStrategy;
import com.up.spa.application.component.TurnoValidator;
import com.up.spa.application.exception.ResourceNotFoundException;
import com.up.spa.application.exception.TurnoNoDisponibleException;
import com.up.spa.application.model.*;
import com.up.spa.application.model.dto.request.TurnoDTO;
import com.up.spa.application.model.mappers.TurnoMapper;
import com.up.spa.application.repository.TurnoRepository;
import com.up.spa.application.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;



@ExtendWith(MockitoExtension.class)
class TurnoServiceImplTest {

  @Mock
  private TurnoRepository turnoRepository;

  @Mock
  private ISucursalService sucursalService;

  @Mock
  private IClienteService clienteService;

  @Mock
  private TurnoMapper turnoMapper;

  @Mock
  private TurnoValidator turnoValidator;

  @Mock
  @Qualifier("estrategiasCancelacion")
  private Map<String, CancelacionStrategy> estrategiasCancelacion;

  @Mock
  private DisponibilidadValidatorService disponibilidadValidator;

  @Mock
  private ServicioAsignacionProfesional servicioAsignacionProfesional;

  @Mock
  private IHorarioService horarioService;

  @InjectMocks
  private TurnoServiceImpl turnoService;

  private TurnoDTO turnoDto;
  private Sucursal sucursal;
  private Servicio servicio;
  private Cliente cliente;
  private Profesional profesional;
  private Horario horario;
  private Turno turno;
  private CancelacionStrategy cancelacionStrategy;

  private final SimpleDateFormat sdfFechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm");

  @BeforeEach
  void setUp() throws ParseException {
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
        .especialidades(Arrays.asList(Especialidad.builder().id("especialidad1").nombre("Corte").build()))
        .servicios(Arrays.asList(servicio)) // Asegúrate de inicializar los servicios
        .build();

    // Inicializar Servicio de prueba
    servicio = Servicio.builder()
        .id("servicio1")
        .nombre("Corte de Pelo")
        .duracion(60)
        .especialidadId("especialidad1")
        .build();

    // Inicializar Cliente de prueba
    cliente = Cliente.builder()
        .id("cliente1")
        .nombre("Maria Lopez")
        .email("maria@example.com")
        .turnos(new ArrayList<>())
        .build();

    // Inicializar TurnoDTO de prueba
    turnoDto = TurnoDTO.builder()
        .id(null)
        .sucursalId("sucursal1")
        .email("maria@example.com")
        .servicioId("servicio1")
        .fecha("2024-04-29")
        .hora("10:00")
        .profesionalId(null) // Sin profesional especificado
        .build();

    // Inicializar Turno de prueba con fecha como Date
    Date fechaTurno = sdfFechaHora.parse("2024-04-29 10:00");
    turno = Turno.builder()
        .id("turno1")
        .sucursal(sucursal)
        .cliente(cliente)
        .servicio(servicio)
        .fecha(fechaTurno)
        .hora("10:00")
        .estado("ACTIVO") // Asegúrate de inicializar este campo si es necesario
        .profesional(profesional)
        .profesionalSeleccionadoPorCliente(false)
        .build();

    // Inicializar CancelacionStrategy de prueba
    cancelacionStrategy = mock(CancelacionStrategy.class);
  }

  // ### 4.1. `cancelarTurno` Tests ###

  // Test para cancelarTurno exitosamente con rol autorizado
  @Test
  void testCancelarTurno_Success() {
    // Arrange
    String turnoId = "turno1";
    String email = "maria@example.com";
    String rol = "ROLE_ADMIN";

    when(turnoRepository.findById(turnoId)).thenReturn(Optional.of(turno));
    when(estrategiasCancelacion.get(rol)).thenReturn(cancelacionStrategy);

    // Act
    turnoService.cancelarTurno(turnoId, email, rol);

    // Assert
    verify(turnoRepository, times(1)).findById(turnoId);
    verify(estrategiasCancelacion, times(1)).get(rol);
    verify(cancelacionStrategy, times(1)).cancelarTurno(turno, email);
    verify(turnoRepository, times(1)).save(turno);
  }

  // Test para cancelarTurno con rol no autorizado
  @Test
  void testCancelarTurno_RolNoAutorizado() {
    // Arrange
    String turnoId = "turno1";
    String email = "maria@example.com";
    String rol = "ROLE_USER"; // Rol no autorizado

    when(turnoRepository.findById(turnoId)).thenReturn(Optional.of(turno));
    when(estrategiasCancelacion.get(rol)).thenReturn(null);

    // Act & Assert
    TurnoNoDisponibleException exception = assertThrows(TurnoNoDisponibleException.class, () -> {
      turnoService.cancelarTurno(turnoId, email, rol);
    }, "Se debería lanzar una TurnoNoDisponibleException para roles no autorizados");

    assertEquals("Rol de usuario no autorizado para cancelar turnos.", exception.getMessage());

    verify(turnoRepository, times(1)).findById(turnoId);
    verify(estrategiasCancelacion, times(1)).get(rol);
    verify(cancelacionStrategy, never()).cancelarTurno(any(), anyString());
    verify(turnoRepository, never()).save(any());
  }

  // Test para cancelarTurno con turno que no existe
  @Test
  void testCancelarTurno_TurnoNoEncontrado() {
    // Arrange
    String turnoId = "nonExistentTurno";
    String email = "maria@example.com";
    String rol = "ROLE_ADMIN";

    when(turnoRepository.findById(turnoId)).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      turnoService.cancelarTurno(turnoId, email, rol);
    }, "Se debería lanzar una ResourceNotFoundException si el turno no existe");

    assertEquals("Turno no encontrado", exception.getMessage());

    verify(turnoRepository, times(1)).findById(turnoId);
    verify(estrategiasCancelacion, never()).get(anyString());
    verify(cancelacionStrategy, never()).cancelarTurno(any(), anyString());
    verify(turnoRepository, never()).save(any());
  }

  // ### 4.2. `listarTurnos` Tests ###

  // Test para listarTurnos como profesional
  @Test
  void testListarTurnos_RoleProfessional() {
    // Arrange
    String email = "juan@example.com";
    String rol = "ROLE_PROFESSIONAL";

    List<Turno> turnos = Arrays.asList(turno);

    when(turnoRepository.findAllGroupedByCliente()).thenReturn(turnos);

    // Act
    List<Turno> result = turnoService.listarTurnos(email, rol);

    // Assert
    assertNotNull(result, "La lista de turnos no debería ser nula");
    assertEquals(1, result.size(), "Debería haber un turno en la lista");
    assertEquals(turno, result.get(0), "El turno devuelto debería coincidir con el esperado");

    verify(turnoRepository, times(1)).findAllGroupedByCliente();
    verify(clienteService, never()).findClienteByEmail(anyString());
    verify(turnoRepository, never()).findAllByClienteId(anyString());
  }

  // Test para listarTurnos como cliente con cliente existente
  @Test
  void testListarTurnos_RoleClient_Success() {
    // Arrange
    String email = "maria@example.com";
    String rol = "ROLE_CLIENT";

    List<Turno> turnos = Arrays.asList(turno);

    when(clienteService.findClienteByEmail(email)).thenReturn(Optional.of(cliente));
    when(turnoRepository.findAllByClienteId(cliente.getId())).thenReturn(turnos);

    // Act
    List<Turno> result = turnoService.listarTurnos(email, rol);

    // Assert
    assertNotNull(result, "La lista de turnos no debería ser nula");
    assertEquals(1, result.size(), "Debería haber un turno en la lista");
    assertEquals(turno, result.get(0), "El turno devuelto debería coincidir con el esperado");

    verify(clienteService, times(1)).findClienteByEmail(email);
    verify(turnoRepository, times(1)).findAllByClienteId(cliente.getId());
    verify(turnoRepository, never()).findAllGroupedByCliente();
  }

  // Test para listarTurnos como cliente sin cliente existente
  @Test
  void testListarTurnos_RoleClient_ClienteNoExiste() {
    // Arrange
    String email = "unknown@example.com";
    String rol = "ROLE_CLIENT";

    when(clienteService.findClienteByEmail(email)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      turnoService.listarTurnos(email, rol);
    }, "Se debería lanzar una IllegalArgumentException si el cliente no existe");

    assertEquals("Cliente no encontrado!", exception.getMessage());

    verify(clienteService, times(1)).findClienteByEmail(email);
    verify(turnoRepository, never()).findAllByClienteId(anyString());
    verify(turnoRepository, never()).findAllGroupedByCliente();
  }

  // Test para listarTurnos con rol inválido
  @Test
  void testListarTurnos_RolInvalido() {
    // Arrange
    String email = "maria@example.com";
    String rol = "ROLE_UNKNOWN";

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      turnoService.listarTurnos(email, rol);
    }, "Se debería lanzar una IllegalArgumentException para roles inválidos");

    assertEquals("Rol inválido: " + rol, exception.getMessage());

    verify(turnoRepository, never()).findAllGroupedByCliente();
    verify(clienteService, never()).findClienteByEmail(anyString());
    verify(turnoRepository, never()).findAllByClienteId(anyString());
  }

  // ### 4.3. `crearTurno` Tests ###

  // Test para crearTurno exitosamente sin especificar un profesional
  @Test
  void testCrearTurno_Success_NoProfesionalEspecificado() throws ParseException {
    // Arrange
    String fecha = "2024-04-29";
    String horaInicio = "10:00";
    String horaFin = "11:00";

    Date fechaTurno = sdfFechaHora.parse(fecha + " " + horaInicio);

    when(sucursalService.findById("sucursal1")).thenReturn(Optional.of(sucursal));
    // turnoValidator.validateTurnoData no lanza excepción, se puede omitir el when
    doNothing().when(turnoValidator).validateTurnoData(turnoDto, sucursal);
    when(clienteService.obtenerClientePorEmail("maria@example.com")).thenReturn(cliente);
    when(sucursalService.obtenerServicioDeSucursal("servicio1", sucursal)).thenReturn(Optional.of(servicio));
    when(horarioService.calcularHoraFin(horaInicio, servicio.getDuracion())).thenReturn(horaFin);
    doNothing().when(disponibilidadValidator).validarDisponibilidadCliente(cliente, fecha, horaInicio, horaFin);
    when(servicioAsignacionProfesional.obtenerProfesionalGenerico(
        sucursal.getId(), servicio.getId(), fecha, horaInicio, horaFin))
        .thenReturn(profesional);
    when(turnoMapper.toEntity(turnoDto)).thenReturn(turno);
    when(turnoRepository.save(turno)).thenReturn("turno1");

    // Act
    TurnoDTO result = turnoService.crearTurno(turnoDto);

    // Assert
    assertNotNull(result, "El TurnoDTO devuelto no debería ser nulo");
    assertEquals("turno1", result.getId(), "El ID del turno debería coincidir con el esperado");
    assertEquals("profesional1", result.getProfesionalId(), "El ID del profesional debería coincidir con el esperado");

    verify(sucursalService, times(1)).findById("sucursal1");
    verify(turnoValidator, times(1)).validateTurnoData(turnoDto, sucursal);
    verify(clienteService, times(1)).obtenerClientePorEmail("maria@example.com");
    verify(sucursalService, times(1)).obtenerServicioDeSucursal("servicio1", sucursal);
    verify(horarioService, times(1)).calcularHoraFin(horaInicio, servicio.getDuracion());
    verify(disponibilidadValidator, times(1)).validarDisponibilidadCliente(cliente, fecha, horaInicio, horaFin);
    verify(servicioAsignacionProfesional, times(1)).obtenerProfesionalGenerico(
        sucursal.getId(), servicio.getId(), fecha, horaInicio, horaFin);
    verify(turnoMapper, times(1)).toEntity(turnoDto);
    verify(turnoRepository, times(1)).save(turno);
    verify(clienteService, times(1)).addTurnoToCliente(cliente, "turno1");
  }

  // Test para crearTurno exitosamente especificando un profesional
  @Test
  void testCrearTurno_Success_ProfesionalEspecificado() throws ParseException {
    // Arrange
    String fecha = "2024-04-29";
    String horaInicio = "10:00";
    String horaFin = "11:00";

    turnoDto.setProfesionalId("profesional1");
    Date fechaTurno = sdfFechaHora.parse(fecha + " " + horaInicio);

    when(sucursalService.findById("sucursal1")).thenReturn(Optional.of(sucursal));
    doNothing().when(turnoValidator).validateTurnoData(turnoDto, sucursal);
    when(clienteService.obtenerClientePorEmail("maria@example.com")).thenReturn(cliente);
    when(sucursalService.obtenerServicioDeSucursal("servicio1", sucursal)).thenReturn(Optional.of(servicio));
    when(horarioService.calcularHoraFin(horaInicio, servicio.getDuracion())).thenReturn(horaFin);
    doNothing().when(disponibilidadValidator).validarDisponibilidadCliente(cliente, fecha, horaInicio, horaFin);
    when(disponibilidadValidator.puedeRealizarServicio(profesional, servicio)).thenReturn(true);
    when(disponibilidadValidator.validarDisponibilidadProfesional(profesional, fecha, horaInicio, horaFin)).thenReturn(true);
    when(turnoMapper.toEntity(turnoDto)).thenReturn(turno);
    when(turnoRepository.save(turno)).thenReturn("turno1");

    // Act
    TurnoDTO result = turnoService.crearTurno(turnoDto);

    // Assert
    assertNotNull(result, "El TurnoDTO devuelto no debería ser nulo");
    assertEquals("turno1", result.getId(), "El ID del turno debería coincidir con el esperado");
    assertEquals("profesional1", result.getProfesionalId(), "El ID del profesional debería coincidir con el esperado");

    verify(sucursalService, times(1)).findById("sucursal1");
    verify(turnoValidator, times(1)).validateTurnoData(turnoDto, sucursal);
    verify(clienteService, times(1)).obtenerClientePorEmail("maria@example.com");
    verify(sucursalService, times(1)).obtenerServicioDeSucursal("servicio1", sucursal);
    verify(horarioService, times(1)).calcularHoraFin(horaInicio, servicio.getDuracion());
    verify(disponibilidadValidator, times(1)).validarDisponibilidadCliente(cliente, fecha, horaInicio, horaFin);
    verify(servicioAsignacionProfesional, never()).obtenerProfesionalGenerico(anyString(), anyString(), anyString(), anyString(), anyString());
    verify(turnoMapper, times(1)).toEntity(turnoDto);
    verify(turnoRepository, times(1)).save(turno);
    verify(clienteService, times(1)).addTurnoToCliente(cliente, "turno1");
  }

  // Test para crearTurno con sucursal que no existe
  @Test
  void testCrearTurno_SucursalNoExiste() {
    // Arrange
    TurnoDTO invalidTurnoDto = TurnoDTO.builder()
        .id(null)
        .sucursalId("nonExistentSucursal")
        .email("maria@example.com")
        .servicioId("servicio1")
        .fecha("2024-04-29")
        .hora("10:00")
        .profesionalId(null)
        .build();

    when(sucursalService.findById("nonExistentSucursal")).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      turnoService.crearTurno(invalidTurnoDto);
    }, "Se debería lanzar una ResourceNotFoundException si la sucursal no existe");

    assertEquals("No se encontró la sucursal con ID: nonExistentSucursal", exception.getMessage());

    verify(sucursalService, times(1)).findById("nonExistentSucursal");
    verify(turnoValidator, never()).validateTurnoData(any(), any());
    verify(clienteService, never()).obtenerClientePorEmail(anyString());
    verify(sucursalService, never()).obtenerServicioDeSucursal(anyString(), any());
    verify(horarioService, never()).calcularHoraFin(anyString(), anyInt());
    verify(disponibilidadValidator, never()).validarDisponibilidadCliente(any(), anyString(), anyString(), anyString());
    verify(servicioAsignacionProfesional, never()).obtenerProfesionalGenerico(anyString(), anyString(), anyString(), anyString(), anyString());
    verify(turnoMapper, never()).toEntity(any());
    verify(turnoRepository, never()).save(any());
    verify(clienteService, never()).addTurnoToCliente(any(), anyString());
  }

  // Test para crearTurno con servicio que no existe
  @Test
  void testCrearTurno_ServicioNoExiste() throws ParseException {
    // Arrange
    when(sucursalService.findById("sucursal1")).thenReturn(Optional.of(sucursal));
    doNothing().when(turnoValidator).validateTurnoData(turnoDto, sucursal);
    when(clienteService.obtenerClientePorEmail("maria@example.com")).thenReturn(cliente);

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      turnoService.crearTurno(turnoDto);
    }, "Se debería lanzar una ResourceNotFoundException si el servicio no existe");

    assertEquals("Servicio no encontrado", exception.getMessage());

    verify(sucursalService, times(1)).findById("sucursal1");
    verify(turnoValidator, times(1)).validateTurnoData(turnoDto, sucursal);
    verify(clienteService, times(1)).obtenerClientePorEmail("maria@example.com");
    verify(horarioService, never()).calcularHoraFin(anyString(), anyInt());
    verify(disponibilidadValidator, never()).validarDisponibilidadCliente(any(), anyString(), anyString(), anyString());
    verify(servicioAsignacionProfesional, never()).obtenerProfesionalGenerico(anyString(), anyString(), anyString(), anyString(), anyString());
    verify(turnoMapper, never()).toEntity(any());
    verify(turnoRepository, never()).save(any());
    verify(clienteService, never()).addTurnoToCliente(any(), anyString());
  }

  // Test para crearTurno con disponibilidad del cliente inválida
  @Test
  void testCrearTurno_DisponibilidadClienteInvalida() throws ParseException {
    // Arrange
    String fecha = "2024-04-29";
    String horaInicio = "10:00";
    String horaFin = "11:00";

    Date fechaTurno = sdfFechaHora.parse(fecha + " " + horaInicio);

    when(sucursalService.findById("sucursal1")).thenReturn(Optional.of(sucursal));
    doNothing().when(turnoValidator).validateTurnoData(turnoDto, sucursal);
    when(clienteService.obtenerClientePorEmail("maria@example.com")).thenReturn(cliente);
    when(sucursalService.obtenerServicioDeSucursal("servicio1", sucursal)).thenReturn(Optional.of(servicio));
    when(horarioService.calcularHoraFin(horaInicio, servicio.getDuracion())).thenReturn(horaFin);
    doThrow(new TurnoNoDisponibleException("El cliente no está disponible en el horario solicitado."))
        .when(disponibilidadValidator).validarDisponibilidadCliente(cliente, fecha, horaInicio, horaFin);

    // Act & Assert
    TurnoNoDisponibleException exception = assertThrows(TurnoNoDisponibleException.class, () -> {
      turnoService.crearTurno(turnoDto);
    }, "Se debería lanzar una TurnoNoDisponibleException si la disponibilidad del cliente es inválida");

    assertEquals("El cliente no está disponible en el horario solicitado.", exception.getMessage());

    verify(sucursalService, times(1)).findById("sucursal1");
    verify(turnoValidator, times(1)).validateTurnoData(turnoDto, sucursal);
    verify(clienteService, times(1)).obtenerClientePorEmail("maria@example.com");
    verify(sucursalService, times(1)).obtenerServicioDeSucursal("servicio1", sucursal);
    verify(horarioService, times(1)).calcularHoraFin(horaInicio, servicio.getDuracion());
    verify(disponibilidadValidator, times(1)).validarDisponibilidadCliente(cliente, fecha, horaInicio, horaFin);
    verify(servicioAsignacionProfesional, never()).obtenerProfesionalGenerico(anyString(), anyString(), anyString(), anyString(), anyString());
    verify(turnoMapper, never()).toEntity(any());
    verify(turnoRepository, never()).save(any());
    verify(clienteService, never()).addTurnoToCliente(any(), anyString());
  }

  // Test para crearTurno especificando un profesional que no existe en la sucursal
  @Test
  void testCrearTurno_ProfesionalEspecificadoNoExiste() throws ParseException {
    // Arrange
    String fecha = "2024-04-29";
    String horaInicio = "10:00";
    String horaFin = "11:00";

    turnoDto.setProfesionalId("nonExistentProfesional");

    when(sucursalService.findById("sucursal1")).thenReturn(Optional.of(sucursal));
    doNothing().when(turnoValidator).validateTurnoData(turnoDto, sucursal);
    when(clienteService.obtenerClientePorEmail("maria@example.com")).thenReturn(cliente);
    when(sucursalService.obtenerServicioDeSucursal("servicio1", sucursal)).thenReturn(Optional.of(servicio));
    when(horarioService.calcularHoraFin(horaInicio, servicio.getDuracion())).thenReturn(horaFin);
    doNothing().when(disponibilidadValidator).validarDisponibilidadCliente(cliente, fecha, horaInicio, horaFin);


    // Act & Assert
    TurnoNoDisponibleException exception = assertThrows(TurnoNoDisponibleException.class, () -> {
      turnoService.crearTurno(turnoDto);
    }, "Se debería lanzar una TurnoNoDisponibleException si el profesional especificado no existe en la sucursal");

    assertEquals("El profesional especificado no está asociado a la sucursal.", exception.getMessage());

    verify(sucursalService, times(1)).findById("sucursal1");
    verify(turnoValidator, times(1)).validateTurnoData(turnoDto, sucursal);
    verify(clienteService, times(1)).obtenerClientePorEmail("maria@example.com");
    verify(sucursalService, times(1)).obtenerServicioDeSucursal("servicio1", sucursal);
    verify(horarioService, times(1)).calcularHoraFin(horaInicio, servicio.getDuracion());
    verify(disponibilidadValidator, times(1)).validarDisponibilidadCliente(cliente, fecha, horaInicio, horaFin);
    verify(servicioAsignacionProfesional, never()).obtenerProfesionalGenerico(anyString(), anyString(), anyString(), anyString(), anyString());
    verify(turnoMapper, never()).toEntity(any());
    verify(turnoRepository, never()).save(any());
    verify(clienteService, never()).addTurnoToCliente(any(), anyString());
  }

  // Test para crearTurno cuando la especialidad requerida no está disponible en la sucursal
  @Test
  void testCrearTurno_EspecialidadNoDisponible() throws ParseException {
    // Arrange
    // Crear una sucursal sin la especialidad requerida
    Sucursal sucursalSinEspecialidad = Sucursal.builder()
        .id("sucursal2")
        .nombre("Sucursal Sin Especialidad")
        .direccion("Calle Verdadera 456")
        .profesionales(Arrays.asList(profesional))
        .especialidades(Arrays.asList()) // Sin especialidades
        .servicios(Arrays.asList(servicio)) // Asegúrate de inicializar los servicios
        .build();

    TurnoDTO turnoDtoConSucursalSinEspecialidad = TurnoDTO.builder()
        .id(null)
        .sucursalId("sucursal2")
        .email("maria@example.com")
        .servicioId("servicio1")
        .fecha("2024-04-29")
        .hora("10:00")
        .profesionalId(null)
        .build();

    when(sucursalService.findById("sucursal2")).thenReturn(Optional.of(sucursalSinEspecialidad));
    doNothing().when(turnoValidator).validateTurnoData(turnoDtoConSucursalSinEspecialidad, sucursalSinEspecialidad);
    when(clienteService.obtenerClientePorEmail("maria@example.com")).thenReturn(cliente);
    when(sucursalService.obtenerServicioDeSucursal("servicio1", sucursalSinEspecialidad)).thenReturn(Optional.of(servicio));

    // Act & Assert
    TurnoNoDisponibleException exception = assertThrows(TurnoNoDisponibleException.class, () -> {
      turnoService.crearTurno(turnoDtoConSucursalSinEspecialidad);
    }, "Se debería lanzar una TurnoNoDisponibleException si la especialidad no está disponible en la sucursal");

    assertEquals("La especialidad requerida no está disponible en la sucursal.", exception.getMessage());

    verify(sucursalService, times(1)).findById("sucursal2");
    verify(turnoValidator, times(1)).validateTurnoData(turnoDtoConSucursalSinEspecialidad, sucursalSinEspecialidad);
    verify(clienteService, times(1)).obtenerClientePorEmail("maria@example.com");
    verify(sucursalService, times(1)).obtenerServicioDeSucursal("servicio1", sucursalSinEspecialidad);
    verify(disponibilidadValidator, never()).validarDisponibilidadCliente(any(), anyString(), anyString(), anyString());
    verify(servicioAsignacionProfesional, never()).obtenerProfesionalGenerico(anyString(), anyString(), anyString(), anyString(), anyString());
    verify(turnoRepository, never()).save(any());
    verify(clienteService, never()).addTurnoToCliente(any(), anyString());
  }


}
