package com.ethan.byztine.domain.exploration;

import com.ethan.byztine.domain.inventory.ItemPreset;
import com.ethan.byztine.domain.inventory.ItemRarity;

import java.util.List;
import java.util.Locale;

public enum ExplorationEncounter {
    CAVE_SCOUT(
            ExplorationZone.BANDIT_CAVE,
            "cave-scout",
            "Cave Scout",
            1,
            5,
            5,
            7,
            6,
            false,
            3,
            26,
            8,
            16,
            45,
            List.of(
                    new ExplorationLootEntry(ItemPreset.LOOKOUT_CLASP, ItemRarity.COMMON, 1, 85),
                    new ExplorationLootEntry(ItemPreset.LOOKOUT_CLASP, ItemRarity.UNCOMMON, 1, 15))),
    RUSTBLADE_RAIDER(
            ExplorationZone.BANDIT_CAVE,
            "rustblade-raider",
            "Rustblade Raider",
            1,
            7,
            5,
            6,
            5,
            false,
            3,
            28,
            8,
            18,
            45,
            List.of(
                    new ExplorationLootEntry(ItemPreset.RUSTED_KOPIS, ItemRarity.COMMON, 1, 85),
                    new ExplorationLootEntry(ItemPreset.RUSTED_KOPIS, ItemRarity.UNCOMMON, 1, 15))),
    DESERTER_GUARD(
            ExplorationZone.BANDIT_CAVE,
            "deserter-guard",
            "Deserter Guard",
            2,
            8,
            5,
            5,
            5,
            false,
            3,
            34,
            10,
            22,
            50,
            List.of(
                    new ExplorationLootEntry(ItemPreset.PILFERED_LAMELLAR, ItemRarity.COMMON, 2, 80),
                    new ExplorationLootEntry(ItemPreset.PILFERED_LAMELLAR, ItemRarity.UNCOMMON, 2, 20))),
    CATACOMB_LOOTER(
            ExplorationZone.BANDIT_CAVE,
            "catacomb-looter",
            "Catacomb Looter",
            2,
            5,
            6,
            7,
            7,
            false,
            3,
            34,
            10,
            24,
            50,
            List.of(
                    new ExplorationLootEntry(ItemPreset.SMUGGLER_RING, ItemRarity.COMMON, 2, 80),
                    new ExplorationLootEntry(ItemPreset.SMUGGLER_RING, ItemRarity.UNCOMMON, 2, 20))),
    BANDIT_CHIEF(
            ExplorationZone.BANDIT_CAVE,
            "bandit-chief",
            "Bandit Chief",
            3,
            10,
            6,
            9,
            7,
            true,
            3,
            52,
            15,
            38,
            100,
            List.of(
                    new ExplorationLootEntry(ItemPreset.OUTLAW_HELM, ItemRarity.COMMON, 3, 45),
                    new ExplorationLootEntry(ItemPreset.OUTLAW_HELM, ItemRarity.UNCOMMON, 3, 20),
                    new ExplorationLootEntry(ItemPreset.STOLEN_ICON, ItemRarity.COMMON, 3, 25),
                    new ExplorationLootEntry(ItemPreset.STOLEN_ICON, ItemRarity.UNCOMMON, 3, 10)));

    private final ExplorationZone zone;
    private final String id;
    private final String displayName;
    private final int level;
    private final int strength;
    private final int intelligence;
    private final int agility;
    private final int luck;
    private final boolean boss;
    private final int energyCost;
    private final int winExperience;
    private final int lossExperience;
    private final int goldReward;
    private final int dropChance;
    private final List<ExplorationLootEntry> lootTable;

    ExplorationEncounter(
            ExplorationZone zone,
            String id,
            String displayName,
            int level,
            int strength,
            int intelligence,
            int agility,
            int luck,
            boolean boss,
            int energyCost,
            int winExperience,
            int lossExperience,
            int goldReward,
            int dropChance,
            List<ExplorationLootEntry> lootTable) {

        if (zone == null) {
            throw new IllegalArgumentException("Exploration zone cannot be null");
        }

        if (level < 1) {
            throw new IllegalArgumentException("Encounter level must be at least 1");
        }

        if (energyCost <= 0) {
            throw new IllegalArgumentException("Encounter energy cost must be positive");
        }

        if (winExperience <= 0 || lossExperience <= 0) {
            throw new IllegalArgumentException("Encounter experience rewards must be positive");
        }

        if (goldReward < 0) {
            throw new IllegalArgumentException("Encounter gold reward cannot be negative");
        }

        if (dropChance < 0 || dropChance > 100) {
            throw new IllegalArgumentException("Encounter drop chance must be between 0 and 100");
        }

        if (lootTable == null || lootTable.isEmpty()) {
            throw new IllegalArgumentException("Encounter loot table cannot be empty");
        }

        this.zone = zone;
        this.id = id;
        this.displayName = displayName;
        this.level = level;
        this.strength = strength;
        this.intelligence = intelligence;
        this.agility = agility;
        this.luck = luck;
        this.boss = boss;
        this.energyCost = energyCost;
        this.winExperience = winExperience;
        this.lossExperience = lossExperience;
        this.goldReward = goldReward;
        this.dropChance = dropChance;
        this.lootTable = List.copyOf(lootTable);
    }

    public ExplorationZone getZone() {
        return zone;
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

    public boolean isBoss() {
        return boss;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public int getWinExperience() {
        return winExperience;
    }

    public int getLossExperience() {
        return lossExperience;
    }

    public int getGoldReward() {
        return goldReward;
    }

    public int getDropChance() {
        return dropChance;
    }

    public List<ExplorationLootEntry> getLootTable() {
        return lootTable;
    }

    public static ExplorationEncounter fromId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Exploration encounter not found");
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);

        for (ExplorationEncounter encounter : values()) {
            if (encounter.id.equals(normalized)) {
                return encounter;
            }
        }

        throw new IllegalArgumentException("Exploration encounter not found: " + value);
    }

    public static ExplorationEncounter fromZoneAndId(ExplorationZone zone, String value) {
        ExplorationEncounter encounter = fromId(value);

        if (encounter.zone != zone) {
            throw new IllegalArgumentException("Exploration encounter does not belong to zone: " + zone.getDisplayName());
        }

        return encounter;
    }
}
