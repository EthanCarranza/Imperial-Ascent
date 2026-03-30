package com.ethan.byztine.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.event.TrainingEvent;
import com.ethan.byztine.domain.event.EventResult;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
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
        assertEquals(20, result.getExperienceGained());

        User reloaded = userRepository.findById(user.getId()).get();

        assertEquals(9, reloaded.getCharacter().getEnergy().getCurrentEnergy());
        assertEquals(20, reloaded.getCharacter().getLevel().getCurrentExperience());
        assertEquals(10, reloaded.getCharacter().getGold());
    }

    @Test
    void shouldRejectInvalidTrainingStatWithoutPersistingChanges() {

        User user = new User("cassius", "cassius-invalid@test.com", "hash");
        user.assignCharacter(new Character("Cassius"));

        userRepository.save(user);

        assertThrows(IllegalArgumentException.class,
                () -> executeEventService.executeEvent(
                        user.getId(),
                        new TrainingEvent("invalid")));

        User reloaded = userRepository.findById(user.getId()).orElseThrow();

        assertEquals(10, reloaded.getCharacter().getEnergy().getCurrentEnergy());
        assertEquals(0, reloaded.getCharacter().getLevel().getCurrentExperience());
        assertEquals(0, reloaded.getCharacter().getGold());
        assertEquals(5, reloaded.getCharacter().getStats().getStrength());
        assertEquals(5, reloaded.getCharacter().getStats().getIntelligence());
        assertEquals(5, reloaded.getCharacter().getStats().getAgility());
        assertEquals(5, reloaded.getCharacter().getStats().getLuck());
    }
}
