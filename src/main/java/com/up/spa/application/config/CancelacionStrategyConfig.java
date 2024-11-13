package com.up.spa.application.config;

import com.up.spa.application.component.CancelacionStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CancelacionStrategyConfig {

  private static final String ROLE_CLIENT= "ROLE_CLIENT";
  private static final String ROLE_PROFESSIONAL = "ROLE_PROFESSIONAL";

  @Bean
  @Qualifier("estrategiasCancelacion")
  public Map<String, CancelacionStrategy> estrategiasCancelacion(
      @Qualifier(ROLE_CLIENT) CancelacionStrategy clienteStrategy,
      @Qualifier(ROLE_PROFESSIONAL) CancelacionStrategy profesionalStrategy) {
    return Map.of(
        ROLE_CLIENT, clienteStrategy,
        ROLE_PROFESSIONAL, profesionalStrategy
    );
  }
}
