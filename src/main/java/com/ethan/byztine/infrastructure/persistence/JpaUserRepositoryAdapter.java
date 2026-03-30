package com.ethan.byztine.infrastructure.persistence;

import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository repository;

    public JpaUserRepositoryAdapter(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        repository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        Objects.requireNonNull(id, "Id cannot be null");
        return repository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Objects.requireNonNull(email, "Email cannot be null");
        return repository.findByEmail(email);
    }
}
