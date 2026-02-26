package com.ethan.byztine.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserWithDefaultRole() {
        User user = new User("ethan", "ethan@email.com", "hash123");

        assertNotNull(user.getId());
        assertEquals(User.Role.USER, user.getRole());
        assertFalse(user.isAdmin());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void shouldPromoteUserToAdmin() {
        User user = new User("ethan", "ethan@email.com", "hash123");

        user.promoteToAdmin();

        assertTrue(user.isAdmin());
        assertEquals(User.Role.ADMIN, user.getRole());
    }

    @Test
    void shouldNotAllowEmptyUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("", "email@test.com", "hash"));
    }
}