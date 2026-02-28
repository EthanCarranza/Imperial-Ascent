package com.ethan.byztine.domain.event;

import com.ethan.byztine.domain.Character;

public class TrainingEvent implements GameEvent {

    private static final int ENERGY_COST = 1;
    private static final int XP_REWARD = 50;
    private static final int GOLD_REWARD = 10;

    @Override
    public EventResult execute(Character character) {

        int levelBefore = character.getLevel().getCurrentLevel();

        character.getEnergy().consume(ENERGY_COST);
        character.gainExperience(XP_REWARD);
        character.addGold(GOLD_REWARD);

        int levelAfter = character.getLevel().getCurrentLevel();

        return new EventResult(
                ENERGY_COST,
                XP_REWARD,
                levelBefore,
                levelAfter,
                GOLD_REWARD);
    }
}