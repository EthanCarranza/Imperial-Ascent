package com.ethan.byztine.domain.inventory;

import java.util.Locale;

public enum ItemRarity {
    COMMON("common", "Common", 1.00, 1.00),
    UNCOMMON("uncommon", "Uncommon", 1.10, 1.20),
    RARE("rare", "Rare", 1.25, 1.50),
    EPIC("epic", "Epic", 1.50, 1.90);

    private final String id;
    private final String displayName;
    private final double powerMultiplier;
    private final double valueMultiplier;

    ItemRarity(
            String id,
            String displayName,
            double powerMultiplier,
            double valueMultiplier) {

        this.id = id;
        this.displayName = displayName;
        this.powerMultiplier = powerMultiplier;
        this.valueMultiplier = valueMultiplier;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int scalePower(int baseValue) {
        if (baseValue < 0) {
            throw new IllegalArgumentException("Item bonus cannot be negative");
        }

        if (baseValue == 0) {
            return 0;
        }

        return Math.max(baseValue, (int) Math.ceil(baseValue * powerMultiplier));
    }

    public int scaleValue(int baseValue) {
        if (baseValue < 0) {
            throw new IllegalArgumentException("Item gold value cannot be negative");
        }

        if (baseValue == 0) {
            return 0;
        }

        return Math.max(baseValue, (int) Math.round(baseValue * valueMultiplier));
    }

    public static ItemRarity fromId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Item rarity not found");
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);

        for (ItemRarity rarity : values()) {
            if (rarity.id.equals(normalized)) {
                return rarity;
            }
        }

        throw new IllegalArgumentException("Item rarity not found: " + value);
    }
}
