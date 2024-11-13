package com.up.spa.auth.application;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthenticationService {
  String authenticate(String email, String password);
  UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}