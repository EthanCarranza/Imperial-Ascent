package com.ethan.byztine.domain.event;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.StatScaling;

import java.util.Locale;

public class TrainingEvent implements GameEvent {

    private static final int ENERGY_COST = 1;
    private static final int BASE_XP = 20;
    private static final int GOLD_REWARD = 10;
    private static final int BASE_INTELLIGENCE = 5;
    private static final double XP_BONUS_PER_INTELLIGENCE = 1.75;

    private final String stat;

    public TrainingEvent() {
        this.stat = "str";
    }

    public TrainingEvent(String stat) {
        this.stat = stat;
    }

    @Override
    public EventResult execute(Character character) {

        String normalizedStat = normalizeStat(stat);

        int xpReward = calculateExperienceReward(character);

        EventResult result = character.applyTraining(
                ENERGY_COST,
                xpReward,
                GOLD_REWARD);

        switch (normalizedStat) {
            case "str":
                character.getStats().increaseStrength(1);
                break;
            case "int":
                character.getStats().increaseIntelligence(1);
                break;
            case "agi":
                character.getStats().increaseAgility(1);
                break;
            case "luck":
                character.getStats().increaseLuck(1);
                break;
            default:
                throw new IllegalStateException("Unsupported stat: " + normalizedStat);
        }

        return result;
    }

    private int calculateExperienceReward(Character character) {
        int intelligence = character.getStats().getIntelligence();
        double effectiveIntelligenceBonus = StatScaling.positiveSoftBonus(
                intelligence,
                BASE_INTELLIGENCE);
        int intelligenceBonus = (int) Math.round(effectiveIntelligenceBonus * XP_BONUS_PER_INTELLIGENCE);

        return BASE_XP + intelligenceBonus;
    }

    private String normalizeStat(String requestedStat) {

        if (requestedStat == null) {
            throw new IllegalArgumentException("Invalid stat");
        }

        String normalizedStat = requestedStat.trim().toLowerCase(Locale.ROOT);

        return switch (normalizedStat) {
            case "str", "int", "agi" -> normalizedStat;
            case "luck", "luk" -> "luck";
            default -> throw new IllegalArgumentException("Invalid stat");
        };
    }
}
