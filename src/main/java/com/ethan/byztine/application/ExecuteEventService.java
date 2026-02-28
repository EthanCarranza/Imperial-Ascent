package com.ethan.byztine.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.event.GameEvent;
import com.ethan.byztine.domain.event.EventResult;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;

import java.util.UUID;

@Service
@Transactional
public class ExecuteEventService {

    private final UserRepository userRepository;

    public ExecuteEventService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public EventResult executeEvent(UUID userId, GameEvent event) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Character character = user.getCharacter();

        if (character == null) {
            throw new IllegalStateException("User has no character");
        }

        EventResult result = character.executeEvent(event);

        return result;
    }
}