package com.ethan.byztine.domain;

import jakarta.persistence.*;

@Embeddable
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

    public boolean addExperience(int amount) {

        if (amount < 0) {
            throw new IllegalArgumentException("Experience must be positive");
        }

        currentExperience += amount;

        boolean leveledUp = false;

        while (true) {
            int required = experienceRequiredForNextLevel();

            if (currentExperience < required) {
                break;
            }

            currentExperience -= required;
            currentLevel++;
            leveledUp = true;
        }

        return leveledUp;
    }
}