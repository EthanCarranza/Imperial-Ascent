package com.ethan.byztine.domain.combat;

public class Combatant {

    private final int attack;
    private final int defense;
    private int currentHealth;

    public Combatant(int attack, int defense, int health) {
        if (health <= 0) {
            throw new IllegalArgumentException("Health must be positive");
        }
        this.attack = attack;
        this.defense = defense;
        this.currentHealth = health;
    }

    public boolean isAlive() {
        return currentHealth > 0;
    }

    public void receiveDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        currentHealth -= damage;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }
}