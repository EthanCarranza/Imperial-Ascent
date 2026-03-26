package com.ethan.byztine.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class Stats {

    private int strength;
    private int intelligence;
    private int agility;

    protected Stats() {
    }

    public Stats(int strength, int intelligence, int agility) {
        this.strength = strength;
        this.intelligence = intelligence;
        this.agility = agility;
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

    public void increaseStrength(int amount) {
        strength += amount;
    }

    public void increaseIntelligence(int amount) {
        intelligence += amount;
    }

    public void increaseAgility(int amount) {
        agility += amount;
    }
}
