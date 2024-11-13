package com.up.spa.application.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
  private int statusCode;
  private String error;
  private String message;
  private List<TurnoSugerencia> sugerencias;
}