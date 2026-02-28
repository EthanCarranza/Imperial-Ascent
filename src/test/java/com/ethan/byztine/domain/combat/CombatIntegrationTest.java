package com.ethan.byztine.domain.combat;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.ethan.byztine.domain.Character;

class CombatIntegrationTest {

    @Test
    void higherLevelCharacterShouldWin() {

        Character levelOne = new Character("Marcus");
        Character levelThree = new Character("Aurelius");

        levelThree.gainExperience(300);

        Combatant c1 = CombatantFactory.fromCharacter(levelOne);
        Combatant c2 = CombatantFactory.fromCharacter(levelThree);

        DamageCalculator calculator = new DefaultDamageCalculator(new DefaultRandomProvider());

        CombatEngine engine = new CombatEngine(calculator);

        CombatResult result = engine.fight(c2, c1);

        assertTrue(result.attackerWon());
    }

    @Test
    void fightShouldEndInDrawIfMaxRoundsReached() {

        Combatant c1 = new Combatant(5, 5, 1000);
        Combatant c2 = new Combatant(5, 5, 1000);

        DamageCalculator calculator = new DefaultDamageCalculator(new DefaultRandomProvider());

        CombatEngine engine = new CombatEngine(calculator);

        CombatResult result = engine.fight(c1, c2);

        assertTrue(result.isDraw());
    }
}