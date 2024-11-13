package com.up.spa.application.repository;

import com.up.spa.application.model.Horario;
import java.util.List;

public interface HorarioRepository {
  List<Horario> findHorariosByProfesionalId(String profesionalId);
}
