package com.ethan.byztine.domain.inventory;

public final class ItemValueCalculator {

    private static final int MIN_GOLD_VALUE = 10;

    private ItemValueCalculator() {
    }

    public static int calculateGoldValue(
            EquipmentSlot slot,
            int level,
            int strengthBonus,
            int intelligenceBonus,
            int agilityBonus,
            int luckBonus,
            int weaponDamage,
            int armor) {

        return calculateGoldValue(
                slot,
                ItemRarity.COMMON,
                level,
                strengthBonus,
                intelligenceBonus,
                agilityBonus,
                luckBonus,
                weaponDamage,
                armor);
    }

    public static int calculateGoldValue(
            EquipmentSlot slot,
            ItemRarity rarity,
            int level,
            int strengthBonus,
            int intelligenceBonus,
            int agilityBonus,
            int luckBonus,
            int weaponDamage,
            int armor) {

        if (slot == null) {
            throw new IllegalArgumentException("Equipment slot cannot be null");
        }

        if (rarity == null) {
            throw new IllegalArgumentException("Item rarity cannot be null");
        }

        if (level < 1) {
            throw new IllegalArgumentException("Item level must be at least 1");
        }

        int baseValue = baseValueFor(slot);
        int statValue = (strengthBonus * 10)
                + (intelligenceBonus * 11)
                + (agilityBonus * 12)
                + (luckBonus * 12);
        int combatValue = (weaponDamage * 14) + (armor * 12);
        double levelMultiplier = 1.0 + ((level - 1) * 0.18);

        return Math.max(
                MIN_GOLD_VALUE,
                rarity.scaleValue((int) Math.round((baseValue + statValue + combatValue) * levelMultiplier)));
    }

    private static int baseValueFor(EquipmentSlot slot) {
        return switch (slot) {
            case WEAPON -> 24;
            case ARMOR -> 22;
            case HELM -> 18;
            case ACCESSORY -> 16;
            case RING -> 18;
            case RELIC -> 20;
        };
    }
}
