package com.ethan.byztine.domain;

import java.util.UUID;

public class Character {

    private UUID id;
    private String name;
    private Level level;
    private Energy energy;

    public Character(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.level = new Level();
        this.energy = new Energy(10);
    }

    public Level getLevel() {
        return level;
    }

    public Energy getEnergy() {
        return energy;
    }

    public void gainExperience(int amount) {
        int previousLevel = level.getCurrentLevel();

        level.addExperience(amount);

        int newLevel = level.getCurrentLevel();

        if (newLevel > previousLevel) {
            int newMaxEnergy = 10 + (newLevel - 1);
            energy.updateMaxEnergy(newMaxEnergy);
        }
    }
}