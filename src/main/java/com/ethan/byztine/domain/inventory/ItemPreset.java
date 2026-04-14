package com.ethan.byztine.domain.inventory;

import java.util.Locale;

public enum ItemPreset {
    TRAINING_SWORD("training-sword", "Training Sword", EquipmentSlot.WEAPON, 0, 0, 0, 0, 2, 0),
    LAMELLAR_ARMOR("lamellar-armor", "Lamellar Armor", EquipmentSlot.ARMOR, 0, 0, 0, 0, 0, 1),
    CAVALRY_CLASP("cavalry-clasp", "Cavalry Clasp", EquipmentSlot.ACCESSORY, 0, 0, 1, 0, 0, 0),
    TAGMATIC_HELM("tagmatic-helm", "Tagmatic Helm", EquipmentSlot.HELM, 0, 0, 0, 0, 0, 1),
    PORPHYRY_RING("porphyry-ring", "Porphyry Ring", EquipmentSlot.RING, 0, 1, 0, 0, 0, 0),
    IMPERIAL_ICON("imperial-icon", "Imperial Icon", EquipmentSlot.RELIC, 0, 0, 0, 1, 0, 0);

    private final String id;
    private final String displayName;
    private final EquipmentSlot slot;
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
            int strengthBonus,
            int intelligenceBonus,
            int agilityBonus,
            int luckBonus,
            int weaponDamage,
            int armor) {

        this.id = id;
        this.displayName = displayName;
        this.slot = slot;
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

    public InventoryItem toItem(int level) {
        return toItem(level, ItemRarity.COMMON);
    }

    public InventoryItem toItem(int level, ItemRarity rarity) {
        return InventoryItem.scaled(
                displayName,
                slot,
                rarity,
                level,
                rarity.scalePower(scaleValue(strengthBonus, level)),
                rarity.scalePower(scaleValue(intelligenceBonus, level)),
                rarity.scalePower(scaleValue(agilityBonus, level)),
                rarity.scalePower(scaleValue(luckBonus, level)),
                rarity.scalePower(scaleValue(weaponDamage, level)),
                rarity.scalePower(scaleValue(armor, level)));
    }

    private int scaleValue(int baseValue, int level) {
        if (level < 1) {
            throw new IllegalArgumentException("Item level must be at least 1");
        }

        if (baseValue == 0) {
            return 0;
        }

        return baseValue + (((level - 1) * baseValue) / 2);
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
