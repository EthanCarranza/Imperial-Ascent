package com.ethan.byztine.domain.combat;

import com.ethan.byztine.domain.Character;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CombatantFactoryTest {

    @Test
    void combatantFactoryShouldMapTrainableStatsIntoCombatStats() {
        Character base = new Character("Base");
        Character trained = new Character("Trained");

        trained.getStats().increaseStrength(2);
        trained.getStats().increaseAgility(3);
        trained.getStats().increaseLuck(4);

        Combatant baseCombatant = CombatantFactory.fromCharacter(base);
        Combatant trainedCombatant = CombatantFactory.fromCharacter(trained);

        assertEquals(baseCombatant.getAttack(), trainedCombatant.getAttack());
        assertEquals(baseCombatant.getDefense(), trainedCombatant.getDefense());
        assertEquals(baseCombatant.getStrength() + 2, trainedCombatant.getStrength());
        assertEquals(baseCombatant.getAgility() + 3, trainedCombatant.getAgility());
        assertEquals(baseCombatant.getLuck() + 4, trainedCombatant.getLuck());
    }
}
