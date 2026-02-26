package com.ethan.byztine.infrastructure;

import com.ethan.byztine.domain.User;
import com.ethan.byztine.domain.UserRepository;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<UUID, User> storage = new HashMap<>();

    @Override
    public void save(User user) {
        storage.put(user.getId(), user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return storage.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
}