package com.up.spa.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


import com.up.spa.application.enums.EstadoTurno;
import com.up.spa.application.model.dto.request.TurnoDTO;
import com.up.spa.application.model.dto.request.TurnoOpcionesRequest;
import com.up.spa.application.model.dto.response.TurnoAgendaResponse;
import com.up.spa.application.services.ITurnoOpcionesService;
import com.up.spa.application.services.ITurnoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


class TurnoControllerTest {

  @Mock
  private ITurnoService turnoService;

  @Mock
  private ITurnoOpcionesService turnoOpcionesService;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private TurnoController turnoController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("test@example.com");
  }

  @Test
  void testCrearTurno_ReturnsTurnoDTO() {
    TurnoDTO turnoDTO = TurnoDTO.builder()
        .fecha("2024-12-01")
        .hora("10:00")
        .servicioId("64df18b9c44e9c2c4fd1a999")
        .sucursalId("64df18b9c44e9c2c4fd1a998")
        .build();
    turnoDTO.setEmail("test@example.com");
    turnoDTO.setEstado(EstadoTurno.ASIGNADO.toString());

    when(turnoService.crearTurno(turnoDTO)).thenReturn(turnoDTO);

    ResponseEntity<TurnoDTO> response = turnoController.crearTurno(turnoDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("test@example.com", response.getBody().getEmail());
    assertEquals(EstadoTurno.ASIGNADO.toString(), response.getBody().getEstado());
    verify(turnoService, times(1)).crearTurno(turnoDTO);
  }

  @Test
  void testObtenerOpcionesTurnos_ReturnsOpciones() {
    String sucursalId = "64df18b9c44e9c2c4fd1a998";
    String fecha = "2024-12-01";
    String servicioId = "64df18b9c44e9c2c4fd1a999";

    TurnoAgendaResponse turnoAgendaResponse = new TurnoAgendaResponse("64df18b9c44e9c2c4fd1a997", "Profesional 1", "Servicio 1", 60, Collections.emptyList());
    when(turnoOpcionesService.obtenerOpcionesTurnos(new TurnoOpcionesRequest(sucursalId, fecha, servicioId)))
        .thenReturn(Arrays.asList(turnoAgendaResponse));

    ResponseEntity<List<TurnoAgendaResponse>> response = turnoController.obtenerOpcionesTurnos(sucursalId, fecha, servicioId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().size());
    verify(turnoOpcionesService, times(1)).obtenerOpcionesTurnos(any(TurnoOpcionesRequest.class));
  }

  @Test
  void testObtenerOpcionesTurnos_ReturnsNoContent() {
    String sucursalId = "64df18b9c44e9c2c4fd1a998";
    String fecha = "2024-12-01";
    String servicioId = "64df18b9c44e9c2c4fd1a999";

    when(turnoOpcionesService.obtenerOpcionesTurnos(new TurnoOpcionesRequest(sucursalId, fecha, servicioId)))
        .thenReturn(Collections.emptyList());

    ResponseEntity<List<TurnoAgendaResponse>> response = turnoController.obtenerOpcionesTurnos(sucursalId, fecha, servicioId);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(turnoOpcionesService, times(1)).obtenerOpcionesTurnos(any(TurnoOpcionesRequest.class));
  }

}
