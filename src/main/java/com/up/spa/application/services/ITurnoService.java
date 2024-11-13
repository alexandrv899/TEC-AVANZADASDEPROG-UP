package com.up.spa.application.services;

import com.up.spa.application.model.Turno;
import com.up.spa.application.model.dto.request.TurnoDTO;
import java.util.List;

public interface ITurnoService {
   TurnoDTO crearTurno(TurnoDTO turno);
   void cancelarTurno(String turnoId, String email, String rol);
   List<Turno> listarTurnos(String email, String rol);
}
