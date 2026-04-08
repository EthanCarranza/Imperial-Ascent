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

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }

        if (slot == null) {
            throw new IllegalArgumentException("Item slot cannot be null");
        }

        if (goldValue < 0) {
            throw new IllegalArgumentException("Item gold value cannot be negative");
        }

        this.name = name;
        this.slot = slot;
        this.goldValue = goldValue;
        this.id = UUID.randomUUID();
        this.strengthBonus = requireNonNegativeBonus(strengthBonus, "Strength");
        this.intelligenceBonus = requireNonNegativeBonus(intelligenceBonus, "Intelligence");
        this.agilityBonus = requireNonNegativeBonus(agilityBonus, "Agility");
        this.luckBonus = requireNonNegativeBonus(luckBonus, "Luck");
        this.equipped = false;
    }

    public static InventoryItem fromPreset(ItemPreset preset) {
        if (preset == null) {
            throw new IllegalArgumentException("Item preset cannot be null");
        }

        return new InventoryItem(
                preset.getDisplayName(),
                preset.getSlot(),
                preset.getGoldValue(),
                preset.getStrengthBonus(),
                preset.getIntelligenceBonus(),
                preset.getAgilityBonus(),
                preset.getLuckBonus());
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
