package com.ethan.byztine.domain.event;

public class EventResult {

    private final int energySpent;
    private final int experienceGained;
    private final int levelBefore;
    private final int levelAfter;

    public EventResult(int energySpent, int experienceGained, int levelBefore, int levelAfter) {
        this.energySpent = energySpent;
        this.experienceGained = experienceGained;
        this.levelBefore = levelBefore;
        this.levelAfter = levelAfter;
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

    public boolean didLevelUp() {
        return levelAfter > levelBefore;
    }
}