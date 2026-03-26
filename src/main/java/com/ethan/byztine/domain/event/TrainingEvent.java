package com.ethan.byztine.domain.event;

import com.ethan.byztine.domain.Character;

public class TrainingEvent implements GameEvent {

    private static final int ENERGY_COST = 1;
    private static final int BASE_XP = 10;
    private static final int INT_MULTIPLIER = 2;
    private static final int GOLD_REWARD = 10;

    private final String stat;

    // Constructor por defecto para tests
    public TrainingEvent() {
        this.stat = "str";
    }

    public TrainingEvent(String stat) {
        this.stat = stat;
    }

    @Override
    public EventResult execute(Character character) {

        int levelBefore = character.getCurrentLevel();

        character.getEnergy().consume(ENERGY_COST);

        int intelligence = character.getStats().getIntelligence();
        int xpReward = BASE_XP + (intelligence * INT_MULTIPLIER);

        character.gainExperience(xpReward);
        character.addGold(GOLD_REWARD);

        // subir stat elegida
        switch (stat == null ? "" : stat.toLowerCase()) {
            case "str":
                character.getStats().increaseStrength(1);
                break;
            case "int":
                character.getStats().increaseIntelligence(1);
                break;
            case "agi":
                character.getStats().increaseAgility(1);
                break;
            default:
                throw new IllegalArgumentException("Invalid stat");
        }

        int levelAfter = character.getCurrentLevel();

        return new EventResult(
                ENERGY_COST,
                xpReward,
                levelBefore,
                levelAfter,
                GOLD_REWARD);
    }
}