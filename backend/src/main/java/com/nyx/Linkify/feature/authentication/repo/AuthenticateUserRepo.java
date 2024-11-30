package com.nyx.Linkify.feature.authentication.repo;

import com.nyx.Linkify.feature.authentication.model.AuthenticationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthenticateUserRepo extends JpaRepository<AuthenticationUser,Long> {


    Optional<AuthenticationUser> findByEmail(String email);
}
