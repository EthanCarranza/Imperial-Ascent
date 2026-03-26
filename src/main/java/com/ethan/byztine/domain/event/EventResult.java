package com.ethan.byztine.domain.event;

public class EventResult {

    private final int energySpent;
    private final int experienceGained;
    private final int levelBefore;
    private final int levelAfter;
    private final int goldGained;

    public EventResult(int energySpent,
            int experienceGained,
            int levelBefore,
            int levelAfter,
            int goldGained) {
        this.energySpent = energySpent;
        this.experienceGained = experienceGained;
        this.levelBefore = levelBefore;
        this.levelAfter = levelAfter;
        this.goldGained = goldGained;
    }

    public int getEnergySpent() {
        return energySpent;
    }

    public int getExperienceGained() {
        return experienceGained;
    }

    public int getLevelBefore() {
        return levelBefore;
    }

    public int getLevelAfter() {
        return levelAfter;
    }

    public int getGoldGained() {
        return goldGained;
    }

    public boolean didLevelUp() {
        return levelAfter > levelBefore;
    }

    @Override
    public String toString() {
        return "Energy: " + energySpent +
                "\nXP: " + experienceGained +
                "\nGold: " + goldGained +
                "\nLevel: " + levelBefore + " → " + levelAfter +
                (didLevelUp() ? "\n LEVEL UP!" : "");
    }
}