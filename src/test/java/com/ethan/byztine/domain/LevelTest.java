package com.ethan.byztine.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelTest {

    @Test
    void newLevelShouldStartAtLevelOneWithZeroExperience() {
        Level level = new Level();

        assertEquals(1, level.getCurrentLevel());
        assertEquals(0, level.getCurrentExperience());
    }

    @Test
    void addingExperienceBelowThresholdShouldNotLevelUp() {
        Level level = new Level();

        level.addExperience(50);

        assertEquals(1, level.getCurrentLevel());
        assertEquals(50, level.getCurrentExperience());
    }

    @Test
    void shouldLevelUpWhenExperienceReachesThreshold() {
        Level level = new Level();

        level.addExperience(100);

        assertEquals(2, level.getCurrentLevel());
        assertEquals(0, level.getCurrentExperience());
    }

    @Test
    void shouldLevelUpMultipleTimesIfEnoughExperienceIsAdded() {
        Level level = new Level();

        level.addExperience(300);

        assertEquals(3, level.getCurrentLevel());
        assertEquals(0, level.getCurrentExperience());
    }

    @Test
    void shouldCarryOverRemainingExperienceAfterMultipleLevelUps() {
        Level level = new Level();

        level.addExperience(350);

        assertEquals(3, level.getCurrentLevel());
        assertEquals(50, level.getCurrentExperience());
    }
}