package com.up.spa.application.component;


import com.up.spa.application.model.Sucursal;
import com.up.spa.application.exception.ResourceNotFoundException;
import com.up.spa.application.exception.InvalidTurnoDataException;
import com.up.spa.application.model.dto.request.TurnoDTO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class TurnoValidator {

  private static final Pattern FECHA_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
  private static final Pattern HORA_PATTERN = Pattern.compile("^(?:[01]\\d|2[0-3]):[0-5]\\d$");


  public void validateTurnoData(TurnoDTO turnoDto, Sucursal sucursal) {
    validateFechaYHora(turnoDto.getFecha(), turnoDto.getHora());
    validateSucursal(sucursal, turnoDto.getSucursalId());
    validateServicio(sucursal, turnoDto.getServicioId());
    validateProfesional(sucursal, turnoDto.getProfesionalId());
  }

  private void validateSucursal(Sucursal sucursal, String sucursalId) {
    if (sucursal == null) {
      throw new ResourceNotFoundException(
          String.format("No se encontró la sucursal con ID: %s", sucursalId)
      );
    }
  }

  private void validateServicio(Sucursal sucursal, String servicioId) {
    boolean servicioExists = sucursal.getServicios().stream()
        .anyMatch(servicio -> servicio.getId().equals(servicioId));

    if (!servicioExists) {
      throw new InvalidTurnoDataException(
          String.format("El servicio con ID %s no está disponible en la sucursal %s",
              servicioId, sucursal.getNombre())
      );
    }
  }

  private void validateProfesional(Sucursal sucursal, String profesionalId) {
    //Es opcional cargar el profesional, solo lo validamos si esta cargado explicitamente
    if (profesionalId == null) {
      return;
    }

    boolean profesionalExists = sucursal.getProfesionales().stream()
        .anyMatch(profesional -> profesional.getId().equals(profesionalId));

    if (!profesionalExists) {
      throw new InvalidTurnoDataException(
          String.format("El profesional con ID %s no pertenece a la sucursal %s",
              profesionalId, sucursal.getNombre())
      );
    }
  }

  private void validateFechaYHora(String fecha, String hora) {

    if (!FECHA_PATTERN.matcher(fecha == null ? "" : fecha).matches()) {
      throw new InvalidTurnoDataException("El formato de la fecha es inválido. Se espera yyyy-MM-dd.");
    }

    if (!HORA_PATTERN.matcher(hora == null ? "" : hora).matches()) {
      throw new InvalidTurnoDataException("El formato de la hora es inválido. Se espera HH:mm.");
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    simpleDateFormat.setLenient(false);

    Calendar calendarActual = Calendar.getInstance();
    Calendar calendarTurno = Calendar.getInstance();

    try {
      Date fechaTurno = simpleDateFormat.parse(fecha + " " + hora);
      calendarTurno.setTime(fechaTurno);

      if (calendarTurno.before(calendarActual)) {
        throw new InvalidTurnoDataException("La fecha y hora seleccionadas son anteriores a la actual.");
      }
    } catch (ParseException e) {
      throw new InvalidTurnoDataException("El formato de fecha y hora es inválido.");
    }
  }

}