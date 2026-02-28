package com.ethan.byztine.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.event.TrainingEvent;
import com.ethan.byztine.domain.event.EventResult;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;

@SpringBootTest
@Transactional
class ExecuteEventServiceTest {

    @Autowired
    private ExecuteEventService executeEventService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldExecuteEventAndPersistCharacterChanges() {

        User user = new User("cassius", "cassius@test.com", "hash");
        user.assignCharacter(new Character("Cassius"));

        userRepository.save(user);

        EventResult result = executeEventService.executeEvent(
                user.getId(),
                new TrainingEvent());

        assertEquals(1, result.getEnergySpent());
        assertEquals(50, result.getExperienceGained());

        User reloaded = userRepository.findById(user.getId()).get();

        assertEquals(9, reloaded.getCharacter().getEnergy().getCurrentEnergy());
        assertEquals(50, reloaded.getCharacter().getLevel().getCurrentExperience());
        assertEquals(10, reloaded.getCharacter().getGold());
    }
}