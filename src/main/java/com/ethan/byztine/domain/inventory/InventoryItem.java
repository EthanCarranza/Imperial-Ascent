package com.ethan.byztine.domain.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentSlot slot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemRarity rarity;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private int goldValue;

    @Column(nullable = false)
    private int strengthBonus;

    @Column(nullable = false)
    private int intelligenceBonus;

    @Column(nullable = false)
    private int agilityBonus;

    @Column(nullable = false)
    private int luckBonus;

    @Column(nullable = false)
    private int weaponDamage;

    @Column(nullable = false)
    private int armor;

    @Column(nullable = false)
    private boolean equipped;

    protected InventoryItem() {
    }

    public InventoryItem(
            String name,
            EquipmentSlot slot,
            int goldValue,
            int strengthBonus,
            int intelligenceBonus,
            int agilityBonus,
            int luckBonus) {
        this(name, slot, ItemRarity.COMMON, 1, goldValue, strengthBonus, intelligenceBonus, agilityBonus, luckBonus, 0, 0);
    }

    public InventoryItem(
            String name,
            EquipmentSlot slot,
            int goldValue,
            int strengthBonus,
            int intelligenceBonus,
            int agilityBonus,
            int luckBonus,
            int weaponDamage,
            int armor) {
        this(name, slot, ItemRarity.COMMON, 1, goldValue, strengthBonus, intelligenceBonus, agilityBonus, luckBonus, weaponDamage, armor);
    }

    public InventoryItem(
            String name,
            EquipmentSlot slot,
            ItemRarity rarity,
            int goldValue,
            int strengthBonus,
            int intelligenceBonus,
            int agilityBonus,
            int luckBonus,
            int weaponDamage,
            int armor) {
        this(name, slot, rarity, 1, goldValue, strengthBonus, intelligenceBonus, agilityBonus, luckBonus, weaponDamage, armor);
    }

    private InventoryItem(
            String name,
            EquipmentSlot slot,
            ItemRarity rarity,
            int level,
            int goldValue,
            int strengthBonus,
            int intelligenceBonus,
            int agilityBonus,
            int luckBonus,
            int weaponDamage,
            int armor) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }

        if (slot == null) {
            throw new IllegalArgumentException("Item slot cannot be null");
        }

        if (rarity == null) {
            throw new IllegalArgumentException("Item rarity cannot be null");
        }

        if (level < 1) {
            throw new IllegalArgumentException("Item level must be at least 1");
        }

        if (goldValue < 0) {
            throw new IllegalArgumentException("Item gold value cannot be negative");
        }

        this.name = name;
        this.slot = slot;
        this.rarity = rarity;
        this.level = level;
        this.goldValue = goldValue;
        this.id = UUID.randomUUID();
        this.strengthBonus = requireNonNegativeBonus(strengthBonus, "Strength");
        this.intelligenceBonus = requireNonNegativeBonus(intelligenceBonus, "Intelligence");
        this.agilityBonus = requireNonNegativeBonus(agilityBonus, "Agility");
        this.luckBonus = requireNonNegativeBonus(luckBonus, "Luck");
        this.weaponDamage = requireNonNegativeBonus(weaponDamage, "Weapon damage");
        this.armor = requireNonNegativeBonus(armor, "Armor");
        this.equipped = false;
    }

    public static InventoryItem scaled(
            String name,
            EquipmentSlot slot,
            int level,
            int strengthBonus,
            int intelligenceBonus,
            int agilityBonus,
            int luckBonus,
            int weaponDamage,
            int armor) {

        return scaled(
                name,
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

    public static InventoryItem scaled(
            String name,
            EquipmentSlot slot,
            ItemRarity rarity,
            int level,
            int strengthBonus,
            int intelligenceBonus,
            int agilityBonus,
            int luckBonus,
            int weaponDamage,
            int armor) {

        return new InventoryItem(
                name,
                slot,
                rarity,
                level,
                ItemValueCalculator.calculateGoldValue(
                        slot,
                        rarity,
                        level,
                        strengthBonus,
                        intelligenceBonus,
                        agilityBonus,
                        luckBonus,
                        weaponDamage,
                        armor),
                strengthBonus,
                intelligenceBonus,
                agilityBonus,
                luckBonus,
                weaponDamage,
                armor);
    }

    public static InventoryItem fromPreset(ItemPreset preset) {
        return fromPreset(preset, 1);
    }

    public static InventoryItem fromPreset(ItemPreset preset, int level) {
        return fromPreset(preset, level, ItemRarity.COMMON);
    }

    public static InventoryItem fromPreset(ItemPreset preset, int level, ItemRarity rarity) {
        if (preset == null) {
            throw new IllegalArgumentException("Item preset cannot be null");
        }

        return preset.toItem(level, rarity);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public int getLevel() {
        return level;
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

    public boolean isEquipped() {
        return equipped;
    }

    public void equip() {
        equipped = true;
    }

    public void unequip() {
        equipped = false;
    }

    public String getBonusSummary() {
        List<String> parts = new ArrayList<>();

        appendBonus(parts, "DMG", weaponDamage);
        appendBonus(parts, "ARM", armor);
        appendBonus(parts, "STR", strengthBonus);
        appendBonus(parts, "INT", intelligenceBonus);
        appendBonus(parts, "AGI", agilityBonus);
        appendBonus(parts, "LUCK", luckBonus);

        if (parts.isEmpty()) {
            return "No bonuses";
        }

        return String.join(" | ", parts);
    }

    private int requireNonNegativeBonus(int value, String label) {
        if (value < 0) {
            throw new IllegalArgumentException(label + " bonus cannot be negative");
        }

        return value;
    }

    private void appendBonus(List<String> parts, String label, int value) {
        if (value > 0) {
            parts.add(label + " +" + value);
        }
    }
}
