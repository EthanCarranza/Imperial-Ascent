package com.ethan.byztine.domain.combat;

public record CombatRoundReport(
        int roundNumber,
        int attackerDamage,
        int defenderDamage,
        int attackerHealthAfterRound,
        int defenderHealthAfterRound,
        boolean defenderActed) {
}
