package com.up.spa.application.services.impl;

import com.up.spa.application.model.Horario;
import com.up.spa.application.repository.HorarioRepository;
import com.up.spa.application.services.IHorarioService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
public class HorarioServiceImpl implements IHorarioService {

  private final HorarioRepository horarioRepository;

  public HorarioServiceImpl(HorarioRepository horarioRepository) {
    this.horarioRepository = horarioRepository;
  }

  @Override
  public List<Horario> obtenerHorariosPorProfesional(String profesionalId) {
    return horarioRepository.findHorariosByProfesionalId(profesionalId);
  }

  @Override
  public String calcularHoraFin(String horaInicio, int duracionMinutos) {
    LocalTime horaInicioTime = LocalTime.parse(horaInicio, DateTimeFormatter.ofPattern("HH:mm"));
    return horaInicioTime.plusMinutes(duracionMinutos).format(DateTimeFormatter.ofPattern("HH:mm"));
  }

  @Override
  public String obtenerDiaSemana(String fecha) {
    LocalDate localDate = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    return localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
  }
}
