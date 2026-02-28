package com.ethan.byztine.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.ethan.byztine.domain.event.GameEvent;
import com.ethan.byztine.domain.event.EventResult;

import com.ethan.byztine.domain.event.TrainingEvent;

public class CharacterTest {
    @Test
    void characterShouldStartWithLevelOneAndBaseEnergy() {
        Character character = new Character("Marcus");

        assertEquals(1, character.getLevel().getCurrentLevel());
        assertEquals(10, character.getEnergy().getCurrentEnergy());
    }

    @Test
    void gainingLevelShouldIncreaseMaxEnergyAndRefill() {
        Character character = new Character("Marcus");

        character.getEnergy().consume(5);

        character.gainExperience(100);

        assertEquals(2, character.getLevel().getCurrentLevel());
        assertEquals(11, character.getEnergy().getMaxEnergy());
        assertEquals(11, character.getEnergy().getCurrentEnergy());
    }

    @Test
    void gainingMultipleLevelsShouldUpdateEnergyOnlyOnceToFinalMax() {
        Character character = new Character("Marcus");

        character.getEnergy().consume(7);

        character.gainExperience(300);

        assertEquals(3, character.getLevel().getCurrentLevel());
        assertEquals(12, character.getEnergy().getMaxEnergy());
        assertEquals(12, character.getEnergy().getCurrentEnergy());
    }

    @Test
    void executingEventShouldConsumeEnergy() {
        Character character = new Character("Marcus");

        GameEvent customEvent = new GameEvent() {
            @Override
            public int energyCost() {
                return 3;
            }

            @Override
            public int experienceReward() {
                return 0;
            }
        };

        character.executeEvent(customEvent);

        assertEquals(7, character.getEnergy().getCurrentEnergy());
    }

    @Test
    void executingEventWithoutEnoughEnergyShouldFail() {
        Character character = new Character("Marcus");

        GameEvent expensiveEvent = new GameEvent() {
            @Override
            public int energyCost() {
                return 10;
            }

            @Override
            public int experienceReward() {
                return 0;
            }
        };

        GameEvent smallEvent = new GameEvent() {
            @Override
            public int energyCost() {
                return 1;
            }

            @Override
            public int experienceReward() {
                return 0;
            }
        };

        character.executeEvent(expensiveEvent);

        assertThrows(IllegalStateException.class, () -> character.executeEvent(smallEvent));
    }

    @Test
    void executingEventShouldReturnEventResult() {
        Character character = new Character("Marcus");
        GameEvent event = new TrainingEvent();

        EventResult result = character.executeEvent(event);

        assertEquals(1, result.getEnergySpent());
        assertEquals(50, result.getExperienceGained());
        assertEquals(1, result.getLevelBefore());
        assertEquals(1, result.getLevelAfter());
    }
}
