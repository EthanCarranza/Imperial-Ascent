package com.ethan.byztine.domain.inventory;

import java.util.Locale;

public enum EquipmentSlot {
    WEAPON("weapon", "Weapon"),
    ARMOR("armor", "Armor"),
    ACCESSORY("accessory", "Accessory"),
    HELM("helm", "Helm"),
    RING("ring", "Ring"),
    RELIC("relic", "Relic");

    private final String id;
    private final String displayName;

    EquipmentSlot(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static EquipmentSlot fromId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Equipment slot not found");
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);

        for (EquipmentSlot slot : values()) {
            if (slot.id.equals(normalized)) {
                return slot;
            }
        }

        throw new IllegalArgumentException("Equipment slot not found: " + value);
    }
}
