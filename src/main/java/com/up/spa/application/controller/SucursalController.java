package com.up.spa.application.controller;

import com.up.spa.application.exception.ResourceNotFoundException;
import com.up.spa.application.model.dto.response.sucursal.SucursalResponse;
import com.up.spa.application.model.Sucursal;
import com.up.spa.application.services.ISucursalService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sucursales")
@Validated
public class SucursalController {

  private final ISucursalService sucursalService;

  public SucursalController(ISucursalService sucursalService) {
    this.sucursalService = sucursalService;
  }

  @GetMapping
  public ResponseEntity<List<SucursalResponse>> getAllSucursales() {
    List<Sucursal> sucursales = sucursalService.getAllSucursales();

    if (sucursales.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    List<SucursalResponse> sucursalResponses = sucursales.stream()
        .map(SucursalResponse::fromModel)
        .collect(Collectors.toList());

    return ResponseEntity.ok(sucursalResponses);
  }

  @GetMapping("/{sucursalId}")
  public ResponseEntity<SucursalResponse> getSucursalById(
      @PathVariable
      @NotBlank(message = "El ID de la sucursal es requerido")
      @Pattern(regexp = "^[0-9a-fA-F]{24}$", message = "El ID de la sucursal es inválido") String sucursalId) {


    Sucursal sucursal = sucursalService.findById(sucursalId)
        .orElseThrow(() -> new ResourceNotFoundException("No se encontró la sucursal con ID: " + sucursalId));

    SucursalResponse sucursalResponse = SucursalResponse.fromModel(sucursal);
    return ResponseEntity.ok(sucursalResponse);
  }


}
