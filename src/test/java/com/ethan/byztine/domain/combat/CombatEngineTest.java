package com.ethan.byztine.domain.combat;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CombatEngineTest {

    @Test
    void strongerAttackerShouldWin() {

        Combatant attacker = new Combatant(10, 2, 20);
        Combatant defender = new Combatant(5, 1, 20);

        DamageCalculator calculator = new DefaultDamageCalculator(bound -> 2);

        CombatEngine engine = new CombatEngine(calculator);

        CombatResult result = engine.fight(attacker, defender);

        assertTrue(result.attackerWon());
    }

    @Test
    void fightDetailedShouldTrackEachRoundUntilKnockout() {
        Combatant attacker = new Combatant(10, 2, 20);
        Combatant defender = new Combatant(5, 1, 20);

        DamageCalculator calculator = new DefaultDamageCalculator(bound -> 2);
        CombatEngine engine = new CombatEngine(calculator);

        CombatReport report = engine.fightDetailed(attacker, defender);

        assertTrue(report.getResult().attackerWon());
        assertEquals(report.getResult().getRounds(), report.getRounds().size());
        assertTrue(report.getRounds().get(0).defenderActed());
    }

    @Test
    void lowerHealthShouldLoseWhenRoundLimitIsReached() {
        Combatant attacker = new Combatant(5, 5, 10);
        Combatant defender = new Combatant(5, 5, 20);

        CombatEngine engine = new CombatEngine((left, right) -> 0);

        CombatReport report = engine.fightDetailed(attacker, defender);

        assertFalse(report.getResult().attackerWon());
        assertFalse(report.getResult().isDraw());
        assertTrue(report.reachedRoundLimit());
        assertEquals(20, report.getResult().getRounds());
    }
}
