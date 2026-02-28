package com.ethan.byztine.domain;

import com.ethan.byztine.domain.event.GameEvent;

import jakarta.persistence.*;

import com.ethan.byztine.domain.event.EventResult;

import java.util.UUID;

@Entity
@Table(name = "characters")
public class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @Embedded
    private Level level;
    @Embedded
    private Energy energy;
    private static final int BASE_ENERGY = 10;
    private int gold;

    private int calculateMaxEnergy() {
        return BASE_ENERGY + (level.getCurrentLevel() - 1);
    }

    protected Character() {
    }

    public Character(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        this.name = name;
        this.level = new Level();
        this.energy = new Energy(calculateMaxEnergy());
        this.gold = 0;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Level getLevel() {
        return level;
    }

    public Energy getEnergy() {
        return energy;
    }

    public int getGold() {
        return gold;
    }

    public void gainExperience(int amount) {

        boolean leveledUp = level.addExperience(amount);

        if (leveledUp) {
            energy.updateMaxEnergy(calculateMaxEnergy());
        }
    }

    public EventResult executeEvent(GameEvent event) {
        return event.execute(this);
    }

    public void addGold(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Gold amount must be positive");
        }
        this.gold += amount;
    }

    public void spendGold(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > gold) {
            throw new IllegalStateException("Not enough gold");
        }
        this.gold -= amount;
    }

}