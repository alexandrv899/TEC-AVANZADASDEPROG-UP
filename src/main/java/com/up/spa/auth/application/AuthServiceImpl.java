package com.up.spa.auth.application;


import com.up.spa.application.enums.UserType;
import com.up.spa.auth.domain.port.UserPort;
import com.up.spa.application.security.JwtService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthenticationService {

  private static final String ROLE_PROFESSIONAL = "ROLE_PROFESSIONAL";
  private static final String ROLE_CLIENT = "ROLE_CLIENT";

  private final UserPort findUserPort;
  private final JwtService tokenPort;
  private final PasswordEncoder passwordEncoder;

  public AuthServiceImpl(
      UserPort findUserPort,
      JwtService jwtService,
      PasswordEncoder passwordEncoder) {
    this.findUserPort = findUserPort;
    this.tokenPort = jwtService;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public String authenticate(String email, String password) {
    return findUserPort.findByEmail(email)
        .filter(user -> passwordEncoder.matches(password, user.getPassword()))
        .map(user -> {
          String role = user.getUserType().equals(UserType.PROFESSIONAL)
              ? ROLE_PROFESSIONAL
              : ROLE_CLIENT;
          return tokenPort.generateToken(user.getEmail(), role);
        }).orElse(null);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return findUserPort.findByEmail(username)
        .map(user -> User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .authorities(user.getUserType().equals(UserType.PROFESSIONAL)
                ? ROLE_PROFESSIONAL
                : ROLE_CLIENT)
            .build())
        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
  }
}