package com.ethan.byztine.domain.combat;

public class Combatant {

    private final int attack;
    private final int defense;
    private final int strength;
    private final int agility;
    private final int luck;
    private final int weaponDamage;
    private final int armor;
    private int currentHealth;

    public Combatant(int attack, int defense, int health) {
        this(attack, defense, health, 5, 5, 5, 0, 0);
    }

    public Combatant(int attack, int defense, int health, int agility) {
        this(attack, defense, health, 5, agility, 5, 0, 0);
    }

    public Combatant(int attack, int defense, int health, int strength, int agility) {
        this(attack, defense, health, strength, agility, 5, 0, 0);
    }

    public Combatant(int attack, int defense, int health, int strength, int agility, int luck) {
        this(attack, defense, health, strength, agility, luck, 0, 0);
    }

    public Combatant(
            int attack,
            int defense,
            int health,
            int strength,
            int agility,
            int luck,
            int weaponDamage,
            int armor) {
        if (health <= 0) {
            throw new IllegalArgumentException("Health must be positive");
        }
        if (strength <= 0) {
            throw new IllegalArgumentException("Strength must be positive");
        }
        if (agility <= 0) {
            throw new IllegalArgumentException("Agility must be positive");
        }
        if (luck <= 0) {
            throw new IllegalArgumentException("Luck must be positive");
        }
        if (weaponDamage < 0) {
            throw new IllegalArgumentException("Weapon damage cannot be negative");
        }
        if (armor < 0) {
            throw new IllegalArgumentException("Armor cannot be negative");
        }
        this.attack = attack;
        this.defense = defense;
        this.currentHealth = health;
        this.strength = strength;
        this.agility = agility;
        this.luck = luck;
        this.weaponDamage = weaponDamage;
        this.armor = armor;
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

    public int getStrength() {
        return strength;
    }

    public int getAgility() {
        return agility;
    }

    public int getLuck() {
        return luck;
    }

    public int getWeaponDamage() {
        return weaponDamage;
    }

    public int getArmor() {
        return armor;
    }
}
