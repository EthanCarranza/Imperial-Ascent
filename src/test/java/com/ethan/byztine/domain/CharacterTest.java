package com.ethan.byztine.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

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
}
