package com.up.spa.application.controller;

import com.up.spa.application.enums.EstadoTurno;
import com.up.spa.application.model.Turno;
import com.up.spa.application.model.dto.request.TurnoDTO;
import com.up.spa.application.model.dto.request.TurnoOpcionesRequest;
import com.up.spa.application.model.dto.response.TurnoAgendaResponse;
import com.up.spa.application.services.ITurnoOpcionesService;
import com.up.spa.application.services.ITurnoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/turnos")
@Validated
public class TurnoController {


  private final ITurnoService turnoService;
  private final ITurnoOpcionesService  turnoOpcionesService;

  public TurnoController(ITurnoService turnoService, ITurnoOpcionesService turnoOpcionesService) {
    this.turnoService = turnoService;
    this.turnoOpcionesService = turnoOpcionesService;
  }


  @PostMapping
  public ResponseEntity<TurnoDTO> crearTurno(@RequestBody @Valid TurnoDTO turno) {

    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    turno.setEmail(email);
    turno.setEstado(EstadoTurno.ASIGNADO.toString());

    return ResponseEntity.ok(turnoService.crearTurno(turno));
  }


  @GetMapping("/opciones")
  public ResponseEntity<List<TurnoAgendaResponse>> obtenerOpcionesTurnos(
      @RequestParam
      @NotBlank(message = "El ID de la sucursal es requerido")
      @Pattern(regexp = "^[0-9a-fA-F]{24}$", message = "El ID de la sucursal es inválido") String sucursalId,

      @RequestParam
      @NotBlank(message = "La fecha es requerida")
      @Pattern(regexp = "^(19|20)\\d\\d-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$",
          message = "El formato de fecha es inválido (yyyy-MM-dd)") String fecha,

      @RequestParam
      @NotBlank(message = "El ID del servicio es requerido")
      @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "El ID del servicio es inválido") String servicioId) {

    List<TurnoAgendaResponse> opcionesTurnos = turnoOpcionesService.obtenerOpcionesTurnos(
        new TurnoOpcionesRequest(sucursalId, fecha, servicioId)
    );

    if (opcionesTurnos.isEmpty()) {
      return ResponseEntity.noContent().build(); // 204 No Content
    }

    return ResponseEntity.ok(opcionesTurnos); // 200 OK
  }


  @DeleteMapping("/cancelar/{turnoId}")
  public ResponseEntity<String> cancelarTurno(
      @PathVariable @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "El ID del turno es inválido") String turnoId) {

    // Obtener el rol y email del usuario autenticado
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    String rol = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();

    turnoService.cancelarTurno(turnoId, email, rol);
    return ResponseEntity.ok("Turno cancelado exitosamente.");
  }

  @GetMapping
  public ResponseEntity<List<Turno>> listarTurnos() {
    // Obtener el email y rol del usuario autenticado
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    String rol = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next()
        .getAuthority();

    List<Turno> turnos = turnoService.listarTurnos(email, rol);
    return ResponseEntity.ok(turnos);
  }


}
