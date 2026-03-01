package com.ethan.byztine.infrastructure;

import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;

import java.util.*;

@Profile("test")
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<UUID, User> storage = new HashMap<>();

    private void assignId(User user) {
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, UUID.randomUUID());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(User user) {

        if (user.getId() == null) {
            assignId(user);
        }

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