package com.ethan.byztine.domain.exploration;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.combat.CombatEngine;
import com.ethan.byztine.domain.combat.CombatResult;
import com.ethan.byztine.domain.combat.CombatantFactory;
import com.ethan.byztine.domain.combat.DefaultDamageCalculator;
import com.ethan.byztine.domain.inventory.InventoryItem;
import com.ethan.byztine.domain.inventory.ItemPreset;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ExplorationBalanceReportTest {

    private static final int ITERATIONS = 2_000;

    @Test
    void printExplorationSnapshot() {
        BalanceSnapshot baseVsScout = simulate(
                "Base Lv1 vs Cave Scout",
                new FighterSpec(1, 0, 0, 0, 0, List.of()),
                ExplorationEncounter.CAVE_SCOUT);
        BalanceSnapshot baseVsRustblade = simulate(
                "Base Lv1 vs Rustblade Raider",
                new FighterSpec(1, 0, 0, 0, 0, List.of()),
                ExplorationEncounter.RUSTBLADE_RAIDER);
        BalanceSnapshot gearedVsDeserter = simulate(
                "Full Shop Lv2 vs Deserter Guard",
                new FighterSpec(
                        2,
                        0,
                        0,
                        0,
                        0,
                        List.of(
                                new ItemLoadout(ItemPreset.TRAINING_SWORD, 2),
                                new ItemLoadout(ItemPreset.LAMELLAR_ARMOR, 2),
                                new ItemLoadout(ItemPreset.CAVALRY_CLASP, 2),
                                new ItemLoadout(ItemPreset.TAGMATIC_HELM, 2),
                                new ItemLoadout(ItemPreset.PORPHYRY_RING, 2),
                                new ItemLoadout(ItemPreset.IMPERIAL_ICON, 2))),
                ExplorationEncounter.DESERTER_GUARD);
        BalanceSnapshot gearedVsLooter = simulate(
                "Full Shop Lv2 vs Catacomb Looter",
                new FighterSpec(
                        2,
                        0,
                        0,
                        0,
                        0,
                        List.of(
                                new ItemLoadout(ItemPreset.TRAINING_SWORD, 2),
                                new ItemLoadout(ItemPreset.LAMELLAR_ARMOR, 2),
                                new ItemLoadout(ItemPreset.CAVALRY_CLASP, 2),
                                new ItemLoadout(ItemPreset.TAGMATIC_HELM, 2),
                                new ItemLoadout(ItemPreset.PORPHYRY_RING, 2),
                                new ItemLoadout(ItemPreset.IMPERIAL_ICON, 2))),
                ExplorationEncounter.CATACOMB_LOOTER);
        BalanceSnapshot gearedVsChief = simulate(
                "Full Shop Lv3 vs Bandit Chief",
                new FighterSpec(
                        3,
                        0,
                        0,
                        0,
                        0,
                        List.of(
                                new ItemLoadout(ItemPreset.TRAINING_SWORD, 3),
                                new ItemLoadout(ItemPreset.LAMELLAR_ARMOR, 3),
                                new ItemLoadout(ItemPreset.CAVALRY_CLASP, 3),
                                new ItemLoadout(ItemPreset.TAGMATIC_HELM, 3),
                                new ItemLoadout(ItemPreset.PORPHYRY_RING, 3),
                                new ItemLoadout(ItemPreset.IMPERIAL_ICON, 3))),
                ExplorationEncounter.BANDIT_CHIEF);

        System.out.println();
        System.out.println("=== Exploration Snapshot ===");
        print(baseVsScout);
        print(baseVsRustblade);
        print(gearedVsDeserter);
        print(gearedVsLooter);
        print(gearedVsChief);

        assertTrue(baseVsScout.playerWinRate() > 30.0);
        assertTrue(baseVsScout.playerWinRate() < 75.0);
        assertTrue(baseVsRustblade.playerWinRate() > 25.0);
        assertTrue(baseVsRustblade.playerWinRate() < 70.0);
        assertTrue(gearedVsDeserter.playerWinRate() > 55.0);
        assertTrue(gearedVsLooter.playerWinRate() > 55.0);
        assertTrue(gearedVsChief.playerWinRate() > 30.0);
        assertTrue(gearedVsChief.playerWinRate() < 95.0);
    }

    private BalanceSnapshot simulate(
            String label,
            FighterSpec playerSpec,
            ExplorationEncounter encounter) {

        Random random = new Random(42L + label.hashCode());
        CombatEngine engine = new CombatEngine(new DefaultDamageCalculator(random::nextInt));

        int playerWins = 0;
        int enemyWins = 0;
        int draws = 0;

        for (int iteration = 0; iteration < ITERATIONS; iteration += 1) {
            Character player = buildCharacter(playerSpec);
            Character enemy = ExplorationEnemyFactory.create(encounter);
            CombatResult result = engine.fight(
                    CombatantFactory.fromCharacter(player),
                    CombatantFactory.fromCharacter(enemy));

            if (result.isDraw()) {
                draws += 1;
            } else if (result.attackerWon()) {
                playerWins += 1;
            } else {
                enemyWins += 1;
            }
        }

        return new BalanceSnapshot(
                label,
                percentage(playerWins),
                percentage(enemyWins),
                percentage(draws));
    }

    private Character buildCharacter(FighterSpec spec) {
        Character character = new Character("Snapshot");
        levelUpTo(character, spec.level());

        if (spec.strengthBonus() > 0) {
            character.getStats().increaseStrength(spec.strengthBonus());
        }

        if (spec.intelligenceBonus() > 0) {
            character.getStats().increaseIntelligence(spec.intelligenceBonus());
        }

        if (spec.agilityBonus() > 0) {
            character.getStats().increaseAgility(spec.agilityBonus());
        }

        if (spec.luckBonus() > 0) {
            character.getStats().increaseLuck(spec.luckBonus());
        }

        for (ItemLoadout loadout : spec.items()) {
            InventoryItem item = InventoryItem.fromPreset(loadout.preset(), loadout.level());
            character.addItem(item);
            character.equipItem(item.getId());
        }

        return character;
    }

    private void levelUpTo(Character character, int targetLevel) {
        int totalExperience = 0;

        for (int level = 1; level < targetLevel; level += 1) {
            totalExperience += 100 * level;
        }

        if (totalExperience > 0) {
            character.gainExperience(totalExperience);
        }
    }

    private double percentage(int value) {
        return (value * 100.0) / ITERATIONS;
    }

    private void print(BalanceSnapshot snapshot) {
        System.out.printf(
                "%s -> player %.2f%% | enemy %.2f%% | draws %.2f%%%n",
                snapshot.label(),
                snapshot.playerWinRate(),
                snapshot.enemyWinRate(),
                snapshot.drawRate());
    }

    private record FighterSpec(
            int level,
            int strengthBonus,
            int intelligenceBonus,
            int agilityBonus,
            int luckBonus,
            List<ItemLoadout> items) {
    }

    private record ItemLoadout(
            ItemPreset preset,
            int level) {
    }

    private record BalanceSnapshot(
            String label,
            double playerWinRate,
            double enemyWinRate,
            double drawRate) {
    }
}
