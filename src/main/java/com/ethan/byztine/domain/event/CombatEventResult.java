package com.ethan.byztine.domain.event;

import com.ethan.byztine.domain.combat.CombatReport;

public class CombatEventResult extends EventResult {

    private final CombatReport combatReport;

    public CombatEventResult(
            int energySpent,
            int experienceGained,
            int levelBefore,
            int levelAfter,
            int goldGained,
            CombatReport combatReport) {

        super(energySpent, experienceGained, levelBefore, levelAfter, goldGained);
        this.combatReport = combatReport;
    }

    public CombatReport getCombatReport() {
        return combatReport;
    }
}
