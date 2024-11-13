package com.up.spa.application.config;

import com.up.spa.application.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private static final String ROLE_PROFESSIONAL = "PROFESSIONAL";
  private static final String ROLE_CLIENT = "CLIENT";

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/sucursales/**").permitAll()
            .requestMatchers("/api/turnos/opciones").hasRole(ROLE_CLIENT)
            .requestMatchers(HttpMethod.GET, "/api/turnos/**").hasAnyRole(ROLE_PROFESSIONAL, ROLE_CLIENT)
            .requestMatchers(HttpMethod.POST, "/api/turnos/**").hasRole(ROLE_CLIENT) // Solo el cliente puede tomar turnos

            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) -> {
              // Este solo se activa cuando la autenticación falla (por ejemplo, token JWT incorrecto)
              response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                  "Authentication failed: " + authException.getMessage());
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              // Este se activa cuando el usuario autenticado no tiene permisos suficientes
              response.sendError(HttpServletResponse.SC_FORBIDDEN,
                  "Access denied: You do not have permission to perform this action.");
            })
        )
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
