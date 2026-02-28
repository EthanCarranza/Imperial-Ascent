package com.ethan.byztine.domain;

import com.ethan.byztine.domain.event.GameEvent;
import com.ethan.byztine.domain.event.EventResult;

import java.util.UUID;

public class Character {

    private UUID id;
    private String name;
    private Level level;
    private Energy energy;
    private static final int BASE_ENERGY = 10;

    private int calculateMaxEnergy() {
        return BASE_ENERGY + (level.getCurrentLevel() - 1);
    }

    public Character(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.level = new Level();
        this.energy = new Energy(calculateMaxEnergy());
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Level getLevel() {
        return level;
    }

    public Energy getEnergy() {
        return energy;
    }

    public void gainExperience(int amount) {

        boolean leveledUp = level.addExperience(amount);

        if (leveledUp) {
            energy.updateMaxEnergy(calculateMaxEnergy());
        }
    }

    public EventResult executeEvent(GameEvent event) {

        int levelBefore = level.getCurrentLevel();

        energy.consume(event.energyCost());
        gainExperience(event.experienceReward());

        int levelAfter = level.getCurrentLevel();

        return new EventResult(
                event.energyCost(),
                event.experienceReward(),
                levelBefore,
                levelAfter);
    }

}