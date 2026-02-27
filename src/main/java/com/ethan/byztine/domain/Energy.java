package com.ethan.byztine.domain;

public class Energy {

    private int currentEnergy;
    private int maxEnergy;

    public Energy(int maxEnergy) {
        if (maxEnergy <= 0) {
            throw new IllegalArgumentException("Max energy must be positive");
        }
        this.maxEnergy = maxEnergy;
        this.currentEnergy = maxEnergy;
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public void consume(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Energy consumption must be positive");
        }

        if (currentEnergy < amount) {
            throw new IllegalStateException("Not enough energy");
        }

        currentEnergy -= amount;
    }

    public void regenerate(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Regeneration amount must be positive");
        }

        currentEnergy += amount;

        if (currentEnergy > maxEnergy) {
            currentEnergy = maxEnergy;
        }
    }

    public void updateMaxEnergy(int newMax) {
        if (newMax <= 0) {
            throw new IllegalArgumentException("Max energy must be positive");
        }

        this.maxEnergy = newMax;
        this.currentEnergy = newMax;
    }
}