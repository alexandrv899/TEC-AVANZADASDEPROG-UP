package com.up.spa.application.services;

import com.up.spa.application.exception.ResourceNotFoundException;
import com.up.spa.application.exception.TurnoNoDisponibleException;
import com.up.spa.application.model.Profesional;
import com.up.spa.application.model.Servicio;
import com.up.spa.application.model.Sucursal;
import org.springframework.stereotype.Service;

@Service
public class ServicioAsignacionProfesional {

  private final ISucursalService sucursalService;
  private final DisponibilidadValidatorService disponibilidadValidator;

  public ServicioAsignacionProfesional(ISucursalService sucursalService, DisponibilidadValidatorService disponibilidadValidator) {
    this.sucursalService = sucursalService;
    this.disponibilidadValidator = disponibilidadValidator;
  }

  public Profesional obtenerProfesionalGenerico(String sucursalId, String servicioId, String fechaStr, String horaInicio, String horaFin) {
    // Obtener la sucursal usando su ID
    Sucursal sucursal = sucursalService.findById(sucursalId)
        .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

    // Buscar el servicio específico en la sucursal usando su ID
    Servicio servicio = sucursal.getServicios().stream()
        .filter(s -> s.getId().equals(servicioId))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado en la sucursal"));

    // Iterar sobre los profesionales y verificar disponibilidad y capacidad para realizar el servicio
    for (Profesional profesional : sucursal.getProfesionales()) {
      if (disponibilidadValidator.puedeRealizarServicio(profesional, servicio) &&
          disponibilidadValidator.validarDisponibilidadProfesional(profesional, fechaStr, horaInicio, horaFin)) {
        return profesional;  // Retorna el primer profesional que cumpla ambas condiciones
      }
    }

    // Si ningún profesional cumple ambas condiciones, lanza la excepción
    throw new TurnoNoDisponibleException("No hay profesionales disponibles en el horario solicitado.");
  }
}
