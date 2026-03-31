package com.ethan.byztine.domain.combat;

import java.util.Locale;

public enum EnemyPreset {
    RECRUIT("recruit", "Recruit", 1, 5, 5, 5, 5),
    SKIRMISHER("skirmisher", "Skirmisher", 1, 7, 5, 7, 6),
    BRUTE("brute", "Brute", 1, 8, 5, 5, 5),
    DUELIST("duelist", "Duelist", 1, 5, 5, 8, 5),
    GAMBLER("gambler", "Gambler", 1, 5, 5, 5, 8),
    VETERAN("veteran", "Veteran", 3, 7, 5, 7, 6);

    private final String id;
    private final String displayName;
    private final int level;
    private final int strength;
    private final int intelligence;
    private final int agility;
    private final int luck;

    EnemyPreset(
            String id,
            String displayName,
            int level,
            int strength,
            int intelligence,
            int agility,
            int luck) {
        this.id = id;
        this.displayName = displayName;
        this.level = level;
        this.strength = strength;
        this.intelligence = intelligence;
        this.agility = agility;
        this.luck = luck;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
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

    public static EnemyPreset fromId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Enemy preset not found");
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);

        for (EnemyPreset preset : values()) {
            if (preset.id.equals(normalized)) {
                return preset;
            }
        }

        throw new IllegalArgumentException("Enemy preset not found: " + value);
    }
}
