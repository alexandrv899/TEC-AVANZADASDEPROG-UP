package com.up.spa.application.repository;

import com.up.spa.application.model.Turno;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


public interface TurnoRepository {
  String save(Turno turno);
  boolean isClienteOcupado(String clienteId, String fecha, String horaInicio, String horaFin);
  boolean isProfesionalOcupado(String id, String fecha, String horaInicio, String horaFin);
  List<LocalTime[]> getIntervalosOcupados(String profesionalId, String fecha);
  Optional<Turno> findById(String turnoId);
  List<Turno> findAllGroupedByCliente();
  List<Turno> findAllByClienteId(String clienteId);
}
