package com.ethan.byztine.application;

import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.infrastructure.InMemoryUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void shouldRegisterUserSuccessfully() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserService service = new UserService(
                repository,
                new BCryptPasswordEncoder());
        User user = service.register("ethan", "ethan@email.com", "hash123");

        assertNotNull(user.getId());
        assertEquals("ethan@email.com", user.getEmail());
    }

    @Test
    void shouldNotAllowDuplicateEmail() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserService service = new UserService(
                repository,
                new BCryptPasswordEncoder());

        service.register("ethan", "ethan@email.com", "hash123");

        assertThrows(IllegalArgumentException.class,
                () -> service.register("another", "ethan@email.com", "hash456"));
    }
}