package com.ethan.byztine.domain.exploration;

import com.ethan.byztine.domain.combat.CombatReport;
import com.ethan.byztine.domain.event.CombatEventResult;

public class ExplorationEventResult extends CombatEventResult {

    private final String zoneDisplayName;
    private final String encounterDisplayName;
    private final boolean bossEncounter;
    private final int encounterLevel;
    private final boolean victory;
    private final String dropSummary;

    public ExplorationEventResult(
            int energySpent,
            int experienceGained,
            int levelBefore,
            int levelAfter,
            int goldGained,
            CombatReport combatReport,
            String zoneDisplayName,
            String encounterDisplayName,
            boolean bossEncounter,
            int encounterLevel,
            boolean victory,
            String dropSummary) {

        super(energySpent, experienceGained, levelBefore, levelAfter, goldGained, combatReport);
        this.zoneDisplayName = zoneDisplayName;
        this.encounterDisplayName = encounterDisplayName;
        this.bossEncounter = bossEncounter;
        this.encounterLevel = encounterLevel;
        this.victory = victory;
        this.dropSummary = dropSummary;
    }

    public String getZoneDisplayName() {
        return zoneDisplayName;
    }

    public String getEncounterDisplayName() {
        return encounterDisplayName;
    }

    public boolean isBossEncounter() {
        return bossEncounter;
    }

    public int getEncounterLevel() {
        return encounterLevel;
    }

    public boolean isVictory() {
        return victory;
    }

    public String getDropSummary() {
        return dropSummary;
    }

    @Override
    public String toString() {
        String summary = super.toString();

        if (dropSummary == null || dropSummary.isBlank()) {
            return summary;
        }

        return summary + "\nDrop: " + dropSummary;
    }
}
