package com.ethan.byztine.application;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String email, String rawPassword) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);

        User user = new User(username, email, hashedPassword);
        userRepository.save(user);

        return user;
    }
}