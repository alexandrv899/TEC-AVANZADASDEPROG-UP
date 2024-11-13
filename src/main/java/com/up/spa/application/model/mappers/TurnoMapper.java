package com.up.spa.application.model.mappers;

import com.up.spa.application.model.Turno;
import com.up.spa.application.model.dto.request.TurnoDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TurnoMapper {

  private ModelMapper modelMapper;


  public TurnoMapper(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  public TurnoDTO toDto(Turno turno) {
    return modelMapper.map(turno, TurnoDTO.class);
  }

  public Turno toEntity(TurnoDTO turnoDto) {
    return modelMapper.map(turnoDto, Turno.class);
  }
}