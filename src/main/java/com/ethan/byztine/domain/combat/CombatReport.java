package com.ethan.byztine.domain.combat;

import java.util.List;

public class CombatReport {

    private final CombatResult result;
    private final List<CombatRoundReport> rounds;
    private final int attackerFinalHealth;
    private final int defenderFinalHealth;
    private final boolean reachedRoundLimit;

    public CombatReport(
            CombatResult result,
            List<CombatRoundReport> rounds,
            int attackerFinalHealth,
            int defenderFinalHealth,
            boolean reachedRoundLimit) {

        this.result = result;
        this.rounds = List.copyOf(rounds);
        this.attackerFinalHealth = attackerFinalHealth;
        this.defenderFinalHealth = defenderFinalHealth;
        this.reachedRoundLimit = reachedRoundLimit;
    }

    public CombatResult getResult() {
        return result;
    }

    public List<CombatRoundReport> getRounds() {
        return rounds;
    }

    public int getAttackerFinalHealth() {
        return attackerFinalHealth;
    }

    public int getDefenderFinalHealth() {
        return defenderFinalHealth;
    }

    public boolean reachedRoundLimit() {
        return reachedRoundLimit;
    }
}
