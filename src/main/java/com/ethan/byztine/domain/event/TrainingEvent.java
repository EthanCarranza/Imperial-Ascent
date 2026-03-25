package com.ethan.byztine.domain.event;

import com.ethan.byztine.domain.Character;

import java.util.Random;

public class TrainingEvent implements GameEvent {

    private static final int ENERGY_COST = 1;
    private static final int GOLD_REWARD = 10;

    private final Random random;

    // 👉 constructor para producción
    public TrainingEvent() {
        this.random = new Random();
    }

    // 👉 constructor para tests (IMPORTANTE)
    public TrainingEvent(Random random) {
        this.random = random;
    }

    @Override
    public EventResult execute(Character character) {

        int xpReward = 40 + random.nextInt(21); // 40–60 XP

        return character.applyTraining(
                ENERGY_COST,
                xpReward,
                GOLD_REWARD);
    }
}