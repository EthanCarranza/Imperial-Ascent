package com.ethan.byztine.domain.exploration;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.combat.CombatEngine;
import com.ethan.byztine.domain.combat.DefaultDamageCalculator;
import com.ethan.byztine.domain.inventory.InventoryItem;
import com.ethan.byztine.domain.inventory.ItemPreset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExplorationEventTest {

    @Test
    void winningExplorationShouldGrantGoldExperienceAndEncounterLoot() {
        Character raider = new Character("Raider");
        levelUpTo(raider, 3);
        raider.getStats().increaseStrength(3);
        raider.getStats().increaseAgility(2);

        ExplorationEventResult result = new ExplorationEvent(
                createDeterministicEngine(),
                ExplorationEncounter.RUSTBLADE_RAIDER,
                bound -> 0).execute(raider);

        assertTrue(result.isVictory());
        assertEquals(3, result.getEnergySpent());
        assertEquals(28, result.getExperienceGained());
        assertEquals(18, result.getGoldGained());
        assertEquals(1, raider.getInventoryUsage());
        assertEquals("Rusted Kopis", raider.getInventoryItems().get(0).getName());
        assertTrue(result.getDropSummary().contains("Rusted Kopis"));
    }

    @Test
    void losingExplorationShouldGrantOnlyConsolationExperience() {
        Character novice = new Character("Novice");

        ExplorationEventResult result = new ExplorationEvent(
                createDeterministicEngine(),
                ExplorationEncounter.BANDIT_CHIEF,
                bound -> 0).execute(novice);

        assertFalse(result.isVictory());
        assertEquals(3, result.getEnergySpent());
        assertEquals(15, result.getExperienceGained());
        assertEquals(0, result.getGoldGained());
        assertEquals(0, novice.getGold());
        assertEquals(0, novice.getInventoryUsage());
        assertEquals("No loot on defeat", result.getDropSummary());
    }

    @Test
    void fullInventoryShouldDiscardLootWithoutBlockingRewards() {
        Character hoarder = new Character("Hoarder");
        levelUpTo(hoarder, 3);
        hoarder.getStats().increaseStrength(3);
        hoarder.getStats().increaseAgility(2);

        for (int index = 0; index < hoarder.getInventoryCapacity(); index += 1) {
            hoarder.addItem(InventoryItem.fromPreset(ItemPreset.CAVALRY_CLASP, 1));
        }

        ExplorationEventResult result = new ExplorationEvent(
                createDeterministicEngine(),
                ExplorationEncounter.CAVE_SCOUT,
                bound -> 0).execute(hoarder);

        assertTrue(result.isVictory());
        assertEquals(26, result.getExperienceGained());
        assertEquals(16, result.getGoldGained());
        assertEquals(hoarder.getInventoryCapacity(), hoarder.getInventoryUsage());
        assertEquals("Satchel full; loot lost", result.getDropSummary());
    }

    private CombatEngine createDeterministicEngine() {
        return new CombatEngine(new DefaultDamageCalculator(bound -> bound == 5 ? 2 : 50));
    }

    private void levelUpTo(Character character, int targetLevel) {
        int totalExperience = 0;

        for (int level = 1; level < targetLevel; level++) {
            totalExperience += 100 * level;
        }

        character.gainExperience(totalExperience);
    }
}
