package com.ethan.byztine.domain.user;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import com.ethan.byztine.domain.Character;

@Entity
@Table(name = "users")

public class User {

    // Por ahora se queda aquí, tal vez en un futuro se podría separar en un archivo
    // separado si se necesita en otras clases
    public enum Role {
        USER,
        ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "character_id")
    private Character character;

    protected User() {
        // Required by JPA
    }

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

    public void assignCharacter(Character character) {
        if (this.character != null) {
            throw new IllegalStateException("User already has a character");
        }
        this.character = character;
    }

    public UUID getId() {
        return id;
    }

    void setId(UUID id) {
        this.id = id;
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

    public Character getCharacter() {
        return character;
    }
}