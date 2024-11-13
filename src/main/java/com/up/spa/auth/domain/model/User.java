package com.up.spa.auth.domain.model;


import com.up.spa.application.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
  private String email;
  private String password;
  private UserType userType;
}
