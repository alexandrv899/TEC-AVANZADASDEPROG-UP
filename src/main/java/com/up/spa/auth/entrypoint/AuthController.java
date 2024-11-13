package com.up.spa.auth.entrypoint;

import com.up.spa.auth.application.AuthenticationService;
import com.up.spa.auth.application.dto.response.AuthResponse;
import com.up.spa.auth.application.dto.request.AuthRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthenticationService authService;

  public AuthController(AuthenticationService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
    String token = authService.authenticate(request.getEmail(), request.getPassword());
    return token != null ? ResponseEntity.ok(new AuthResponse(token))
        : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }
}
