package com.ethan.byztine.domain.exploration;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.StatScaling;
import com.ethan.byztine.domain.combat.CombatEngine;
import com.ethan.byztine.domain.combat.CombatReport;
import com.ethan.byztine.domain.combat.Combatant;
import com.ethan.byztine.domain.combat.CombatantFactory;
import com.ethan.byztine.domain.combat.RandomProvider;
import com.ethan.byztine.domain.event.GameEvent;
import com.ethan.byztine.domain.inventory.InventoryItem;

import java.util.concurrent.ThreadLocalRandom;

public class ExplorationEvent implements GameEvent {

    private static final int BASE_INTELLIGENCE = 5;
    private static final double XP_BONUS_PER_INTELLIGENCE = 2.5;
    private static final int BASE_LUCK = 5;
    private static final double GOLD_BONUS_PER_LUCK_POINT = 0.03;

    private final CombatEngine engine;
    private final ExplorationEncounter encounter;
    private final RandomProvider randomProvider;

    public ExplorationEvent(CombatEngine engine, ExplorationEncounter encounter) {
        this(engine, encounter, bound -> ThreadLocalRandom.current().nextInt(bound));
    }

    public ExplorationEvent(
            CombatEngine engine,
            ExplorationEncounter encounter,
            RandomProvider randomProvider) {

        if (engine == null) {
            throw new IllegalArgumentException("Combat engine cannot be null");
        }

        if (encounter == null) {
            throw new IllegalArgumentException("Exploration encounter cannot be null");
        }

        if (randomProvider == null) {
            throw new IllegalArgumentException("Random provider cannot be null");
        }

        this.engine = engine;
        this.encounter = encounter;
        this.randomProvider = randomProvider;
    }

    @Override
    public ExplorationEventResult execute(Character character) {
        int levelBefore = character.getCurrentLevel();

        character.getEnergy().consume(encounter.getEnergyCost());

        Character enemy = ExplorationEnemyFactory.create(encounter);
        Combatant player = CombatantFactory.fromCharacter(character);
        Combatant opponent = CombatantFactory.fromCharacter(enemy);

        CombatReport report = engine.fightDetailed(player, opponent);
        boolean victory = report.getResult().attackerWon();
        int experienceGain = victory
                ? calculateExperienceReward(character, encounter.getWinExperience())
                : calculateExperienceReward(character, encounter.getLossExperience());

        character.gainExperience(experienceGain);

        int goldGain = 0;
        String dropSummary = victory ? "No loot found" : "No loot on defeat";

        if (victory) {
            goldGain = calculateGoldReward(character, encounter.getGoldReward());
            character.addGold(goldGain);

            DropOutcome dropOutcome = resolveDrop(character);

            if (dropOutcome.item() != null) {
                character.addItem(dropOutcome.item());
            }

            dropSummary = dropOutcome.summary();
        }

        int levelAfter = character.getCurrentLevel();

        return new ExplorationEventResult(
                encounter.getEnergyCost(),
                experienceGain,
                levelBefore,
                levelAfter,
                goldGain,
                report,
                encounter.getZone().getDisplayName(),
                encounter.getDisplayName(),
                encounter.isBoss(),
                encounter.getLevel(),
                victory,
                dropSummary);
    }

    private int calculateExperienceReward(Character character, int baseReward) {
        int intelligence = character.getEffectiveIntelligence();
        double effectiveIntelligenceBonus = StatScaling.positiveSoftBonus(
                intelligence,
                BASE_INTELLIGENCE);
        int intelligenceBonus = (int) Math.round(effectiveIntelligenceBonus * XP_BONUS_PER_INTELLIGENCE);

        return baseReward + intelligenceBonus;
    }

    private int calculateGoldReward(Character character, int baseReward) {
        int luck = character.getEffectiveLuck();
        double effectiveLuckBonus = StatScaling.positiveSoftBonus(luck, BASE_LUCK);
        double goldMultiplier = 1.0 + (effectiveLuckBonus * GOLD_BONUS_PER_LUCK_POINT);

        return Math.max(1, (int) Math.round(baseReward * goldMultiplier));
    }

    private DropOutcome resolveDrop(Character character) {
        if (randomProvider.nextInt(100) >= encounter.getDropChance()) {
            return new DropOutcome(null, "No loot found");
        }

        if (character.getInventoryUsage() >= character.getInventoryCapacity()) {
            return new DropOutcome(null, "Satchel full; loot lost");
        }

        ExplorationLootEntry entry = selectLootEntry();
        InventoryItem item = entry.createItem();

        return new DropOutcome(
                item,
                item.getName()
                        + " ["
                        + item.getSlot().getDisplayName()
                        + "] "
                        + item.getRarity().getDisplayName()
                        + " Lv."
                        + item.getLevel());
    }

    private ExplorationLootEntry selectLootEntry() {
        int totalWeight = encounter.getLootTable().stream()
                .mapToInt(ExplorationLootEntry::weight)
                .sum();
        int roll = randomProvider.nextInt(totalWeight);
        int currentWeight = 0;

        for (ExplorationLootEntry entry : encounter.getLootTable()) {
            currentWeight += entry.weight();

            if (roll < currentWeight) {
                return entry;
            }
        }

        return encounter.getLootTable().get(encounter.getLootTable().size() - 1);
    }

    private record DropOutcome(
            InventoryItem item,
            String summary) {
    }
}
