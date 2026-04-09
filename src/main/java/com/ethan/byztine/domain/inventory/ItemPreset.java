package com.ethan.byztine.domain.inventory;

import java.util.Locale;

public enum ItemPreset {
    TRAINING_SWORD("training-sword", "Training Sword", EquipmentSlot.WEAPON, 45, 0, 0, 0, 0, 2, 0),
    LAMELLAR_ARMOR("lamellar-armor", "Lamellar Armor", EquipmentSlot.ARMOR, 70, 0, 0, 0, 0, 0, 1),
    CAVALRY_CLASP("cavalry-clasp", "Cavalry Clasp", EquipmentSlot.ACCESSORY, 55, 0, 0, 1, 0, 0, 0),
    TAGMATIC_HELM("tagmatic-helm", "Tagmatic Helm", EquipmentSlot.HELM, 50, 0, 0, 0, 0, 0, 1),
    PORPHYRY_RING("porphyry-ring", "Porphyry Ring", EquipmentSlot.RING, 80, 0, 1, 0, 0, 0, 0),
    IMPERIAL_ICON("imperial-icon", "Imperial Icon", EquipmentSlot.RELIC, 95, 0, 0, 0, 1, 0, 0);

    private final String id;
    private final String displayName;
    private final EquipmentSlot slot;
    private final int goldValue;
    private final int strengthBonus;
    private final int intelligenceBonus;
    private final int agilityBonus;
    private final int luckBonus;
    private final int weaponDamage;
    private final int armor;

    ItemPreset(
            String id,
            String displayName,
            EquipmentSlot slot,
            int goldValue,
            int strengthBonus,
            int intelligenceBonus,
            int agilityBonus,
            int luckBonus,
            int weaponDamage,
            int armor) {

        this.id = id;
        this.displayName = displayName;
        this.slot = slot;
        this.goldValue = goldValue;
        this.strengthBonus = strengthBonus;
        this.intelligenceBonus = intelligenceBonus;
        this.agilityBonus = agilityBonus;
        this.luckBonus = luckBonus;
        this.weaponDamage = weaponDamage;
        this.armor = armor;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public int getGoldValue() {
        return goldValue;
    }

    public int getStrengthBonus() {
        return strengthBonus;
    }

    public int getIntelligenceBonus() {
        return intelligenceBonus;
    }

    public int getAgilityBonus() {
        return agilityBonus;
    }

    public int getLuckBonus() {
        return luckBonus;
    }

    public int getWeaponDamage() {
        return weaponDamage;
    }

    public int getArmor() {
        return armor;
    }

    public static ItemPreset fromId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Item preset not found");
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);

        for (ItemPreset preset : values()) {
            if (preset.id.equals(normalized)) {
                return preset;
            }
        }

        throw new IllegalArgumentException("Item preset not found: " + value);
    }
}
