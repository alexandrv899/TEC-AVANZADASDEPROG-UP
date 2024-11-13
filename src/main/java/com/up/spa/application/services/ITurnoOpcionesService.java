package com.up.spa.application.services;

import com.up.spa.application.model.dto.request.TurnoOpcionesRequest;
import com.up.spa.application.model.dto.response.TurnoAgendaResponse;
import java.util.List;

public interface ITurnoOpcionesService {

  List<TurnoAgendaResponse> obtenerOpcionesTurnos(TurnoOpcionesRequest turnoOpcionesRequest);
}
