package com.ethan.byztine.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class Stats {

    private int strength;
    private int intelligence;
    private int agility;
    private int luck;

    protected Stats() {
    }

    public Stats(int strength, int intelligence, int agility, int luck) {
        this.strength = strength;
        this.intelligence = intelligence;
        this.agility = agility;
        this.luck = luck;
    }

    public int getStrength() {
        return strength;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getAgility() {
        return agility;
    }

    public int getLuck() {
        return luck;
    }

    public void increaseStrength(int amount) {
        requirePositiveAmount(amount);
        strength += amount;
    }

    public void increaseIntelligence(int amount) {
        requirePositiveAmount(amount);
        intelligence += amount;
    }

    public void increaseAgility(int amount) {
        requirePositiveAmount(amount);
        agility += amount;
    }

    public void increaseLuck(int amount) {
        requirePositiveAmount(amount);
        luck += amount;
    }

    private void requirePositiveAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
