package com.ethan.byztine.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatsTest {

    @Test
    void increasingStatsShouldRequirePositiveAmount() {
        Stats stats = new Stats(5, 5, 5, 5);

        assertEquals("Amount must be positive",
                assertThrows(IllegalArgumentException.class,
                        () -> stats.increaseStrength(0)).getMessage());
        assertEquals("Amount must be positive",
                assertThrows(IllegalArgumentException.class,
                        () -> stats.increaseIntelligence(-1)).getMessage());
        assertEquals("Amount must be positive",
                assertThrows(IllegalArgumentException.class,
                        () -> stats.increaseAgility(-5)).getMessage());
        assertEquals("Amount must be positive",
                assertThrows(IllegalArgumentException.class,
                        () -> stats.increaseLuck(-2)).getMessage());
    }

    @Test
    void increasingStatsWithPositiveAmountShouldWork() {
        Stats stats = new Stats(5, 5, 5, 5);

        stats.increaseStrength(2);
        stats.increaseIntelligence(3);
        stats.increaseAgility(4);
        stats.increaseLuck(1);

        assertEquals(7, stats.getStrength());
        assertEquals(8, stats.getIntelligence());
        assertEquals(9, stats.getAgility());
        assertEquals(6, stats.getLuck());
    }
}
