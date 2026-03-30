package com.ethan.byztine.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;

@Service
@Transactional
public class RegistrationService {

    private final UserService userService;
    private final UserRepository userRepository;

    public RegistrationService(UserService userService,
            UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public User registerUserWithCharacter(
            String username,
            String email,
            String rawPassword,
            String characterName) {

        User user = userService.register(username, email, rawPassword);
        Character character = new Character(characterName);

        user.assignCharacter(character);

        userRepository.save(user);

        return user;
    }
}
