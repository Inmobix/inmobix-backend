package com.inmobix.backend.playground.playgraundJordy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserClassRepositoryJDPY
        extends JpaRepository<com.inmobix.backend.playground.playgraundJordy.model.UserClassJDPY, Long> {
    Optional<com.inmobix.backend.playground.playgraundJordy.model.UserClassJDPY> findByEmail(String email);
}
