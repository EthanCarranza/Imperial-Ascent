package com.ethan.byztine.controller;

import org.springframework.web.bind.annotation.*;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;

@RestController
@RequestMapping("/api/debug")
public class DebugApiController {

    private final UserRepository userRepository;

    public DebugApiController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/create-user")
    public User createUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) {

        // ⚠️ Esto es DEBUG, no producción
        String passwordHash = password;

        User user = new User(username, email, passwordHash);
        userRepository.save(user);

        return user;
    }

    @GetMapping("/user")
    public User getUser(@RequestParam String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}