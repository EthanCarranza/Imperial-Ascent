package com.ethan.byztine.domain.user;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    // Por ahora se queda aquí, tal vez en un futuro se podría separar en un archivo
    // separado si se necesita en otras clases
    public enum Role {
        USER,
        ADMIN
    }

    private UUID id;
    private String username;
    private String email;
    private String passwordHash;
    private Role role;
    private LocalDateTime createdAt;

    public User(String username, String email, String passwordHash) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }

        this.id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = Role.USER;
        this.createdAt = LocalDateTime.now();
    }

    public void changePassword(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }
        this.passwordHash = newPasswordHash;
    }

    public boolean hasPasswordHash(String passwordHash) {
        return this.passwordHash.equals(passwordHash);
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public void promoteToAdmin() {
        this.role = Role.ADMIN;
    }

    public void demoteToUser() {
        this.role = Role.USER;
    }

}