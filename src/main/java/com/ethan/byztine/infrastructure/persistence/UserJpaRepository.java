package com.ethan.byztine.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ethan.byztine.domain.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository
        extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
}
