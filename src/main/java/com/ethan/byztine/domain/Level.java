package com.ethan.byztine.domain;

public class Level {

    private int currentLevel;
    private int currentExperience;

    private int experienceRequiredForNextLevel() {
        return 100 * currentLevel;
    }

    public Level() {
        this.currentLevel = 1;
        this.currentExperience = 0;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentExperience() {
        return currentExperience;
    }

    public void addExperience(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Experience amount cannot be negative");
        }

        this.currentExperience += amount;

        while (this.currentExperience >= experienceRequiredForNextLevel()) {
            this.currentExperience -= experienceRequiredForNextLevel();
            this.currentLevel++;
        }
    }
}