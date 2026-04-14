package com.ethan.byztine.domain.exploration;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum ExplorationZone {
    BANDIT_CAVE(
            "bandit-cave",
            "Cueva de Bandidos",
            1,
            3,
            "Una guarida temprana con forajidos, desertores y botin robado.");

    private final String id;
    private final String displayName;
    private final int recommendedLevelMin;
    private final int recommendedLevelMax;
    private final String description;

    ExplorationZone(
            String id,
            String displayName,
            int recommendedLevelMin,
            int recommendedLevelMax,
            String description) {

        this.id = id;
        this.displayName = displayName;
        this.recommendedLevelMin = recommendedLevelMin;
        this.recommendedLevelMax = recommendedLevelMax;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getRecommendedLevelMin() {
        return recommendedLevelMin;
    }

    public int getRecommendedLevelMax() {
        return recommendedLevelMax;
    }

    public String getDescription() {
        return description;
    }

    public List<ExplorationEncounter> getEncounters() {
        return Arrays.stream(ExplorationEncounter.values())
                .filter(encounter -> encounter.getZone() == this)
                .toList();
    }

    public static ExplorationZone fromId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Exploration zone not found");
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);

        for (ExplorationZone zone : values()) {
            if (zone.id.equals(normalized)) {
                return zone;
            }
        }

        throw new IllegalArgumentException("Exploration zone not found: " + value);
    }
}
