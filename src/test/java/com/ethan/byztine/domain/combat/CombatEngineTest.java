package com.ethan.byztine.domain.combat;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CombatEngineTest {

    @Test
    void strongerAttackerShouldWin() {

        Combatant attacker = new Combatant(10, 2, 20);
        Combatant defender = new Combatant(5, 1, 20);

        DamageCalculator calculator = new DefaultDamageCalculator(new FixedRandomProvider());

        CombatEngine engine = new CombatEngine(calculator);

        CombatResult result = engine.fight(attacker, defender);

        assertTrue(result.attackerWon());
    }
}