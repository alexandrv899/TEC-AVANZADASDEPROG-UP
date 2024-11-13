package com.up.spa.application.services;

import com.up.spa.application.model.Horario;
import java.util.List;

public interface IHorarioService {

  List<Horario> obtenerHorariosPorProfesional(String profesionalId);

  String calcularHoraFin(String horaInicio, int duracionMinutos);

  String obtenerDiaSemana(String fecha);
}