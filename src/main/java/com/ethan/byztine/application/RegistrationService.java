package com.ethan.byztine.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;

@Service
@Transactional
public class RegistrationService {

    private final UserRepository userRepository;

    public RegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUserWithCharacter(
            String username,
            String email,
            String passwordHash,
            String characterName) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Email already registered");
        }

        User user = new User(username, email, passwordHash);
        Character character = new Character(characterName);

        user.assignCharacter(character);

        userRepository.save(user);

        return user;
    }
}