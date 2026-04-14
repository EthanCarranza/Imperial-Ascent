package com.ethan.byztine.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.combat.CombatEngine;
import com.ethan.byztine.domain.combat.DefaultDamageCalculator;
import com.ethan.byztine.domain.event.TrainingEvent;
import com.ethan.byztine.domain.event.EventResult;
import com.ethan.byztine.domain.exploration.ExplorationEncounter;
import com.ethan.byztine.domain.exploration.ExplorationEvent;
import com.ethan.byztine.domain.exploration.ExplorationEventResult;
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

    @Test
    void shouldExecuteExplorationEventAndPersistLoot() {

        User user = new User("explorer", "explorer@test.com", "hash");
        Character character = new Character("Explorer");
        levelUpTo(character, 3);
        character.getStats().increaseStrength(3);
        character.getStats().increaseAgility(2);
        user.assignCharacter(character);

        userRepository.save(user);

        ExplorationEventResult result = (ExplorationEventResult) executeEventService.executeEvent(
                user.getId(),
                new ExplorationEvent(
                        new CombatEngine(new DefaultDamageCalculator(bound -> bound == 5 ? 2 : 50)),
                        ExplorationEncounter.CAVE_SCOUT,
                        bound -> 0));

        assertTrue(result.isVictory());
        assertEquals(3, result.getEnergySpent());
        assertEquals(26, result.getExperienceGained());
        assertEquals(16, result.getGoldGained());

        User reloaded = userRepository.findById(user.getId()).orElseThrow();

        assertEquals(9, reloaded.getCharacter().getEnergy().getCurrentEnergy());
        assertEquals(16, reloaded.getCharacter().getGold());
        assertEquals(1, reloaded.getCharacter().getInventoryUsage());
        assertEquals("Lookout Clasp", reloaded.getCharacter().getInventoryItems().get(0).getName());
    }

    private void levelUpTo(Character character, int targetLevel) {
        int totalExperience = 0;

        for (int level = 1; level < targetLevel; level++) {
            totalExperience += 100 * level;
        }

        character.gainExperience(totalExperience);
    }
}
