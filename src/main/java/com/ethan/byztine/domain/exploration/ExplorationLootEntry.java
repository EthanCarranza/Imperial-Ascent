package com.ethan.byztine.domain.exploration;

import com.ethan.byztine.domain.inventory.InventoryItem;
import com.ethan.byztine.domain.inventory.ItemPreset;
import com.ethan.byztine.domain.inventory.ItemRarity;

public record ExplorationLootEntry(
        ItemPreset preset,
        ItemRarity rarity,
        int level,
        int weight) {

    public ExplorationLootEntry {
        if (preset == null) {
            throw new IllegalArgumentException("Loot preset cannot be null");
        }

        if (rarity == null) {
            throw new IllegalArgumentException("Loot rarity cannot be null");
        }

        if (level < 1) {
            throw new IllegalArgumentException("Loot level must be at least 1");
        }

        if (weight <= 0) {
            throw new IllegalArgumentException("Loot weight must be positive");
        }
    }

    public InventoryItem createItem() {
        return InventoryItem.fromPreset(preset, level, rarity);
    }

    public String formatPreview() {
        return preset.getDisplayName()
                + " ["
                + preset.getSlot().getDisplayName()
                + "] "
                + rarity.getDisplayName()
                + " Lv."
                + level;
    }
}
