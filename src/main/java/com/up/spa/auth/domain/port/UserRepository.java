package com.up.spa.auth.domain.port;

import com.up.spa.auth.domain.model.User;
import java.util.Optional;

public interface UserRepository extends UserPort {

  Optional<User> findByEmail(String email);

}
