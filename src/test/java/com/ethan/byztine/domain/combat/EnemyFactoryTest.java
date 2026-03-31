package com.ethan.byztine.domain.combat;

import com.ethan.byztine.domain.Character;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnemyFactoryTest {

    @Test
    void brutePresetShouldCreateStrengthFocusedEnemy() {
        Character brute = EnemyFactory.create(EnemyPreset.BRUTE);

        assertEquals("Brute", brute.getName());
        assertEquals(1, brute.getCurrentLevel());
        assertEquals(8, brute.getStats().getStrength());
        assertEquals(5, brute.getStats().getAgility());
        assertEquals(5, brute.getStats().getLuck());
    }

    @Test
    void veteranPresetShouldCreateHigherLevelMixedEnemy() {
        Character veteran = EnemyFactory.create(EnemyPreset.VETERAN);

        assertEquals("Veteran", veteran.getName());
        assertEquals(3, veteran.getCurrentLevel());
        assertEquals(7, veteran.getStats().getStrength());
        assertEquals(7, veteran.getStats().getAgility());
        assertEquals(6, veteran.getStats().getLuck());
    }

    @Test
    void skirmisherPresetShouldCreateIntermediateEnemy() {
        Character skirmisher = EnemyFactory.create(EnemyPreset.SKIRMISHER);

        assertEquals("Skirmisher", skirmisher.getName());
        assertEquals(1, skirmisher.getCurrentLevel());
        assertEquals(7, skirmisher.getStats().getStrength());
        assertEquals(7, skirmisher.getStats().getAgility());
        assertEquals(6, skirmisher.getStats().getLuck());
    }
}
