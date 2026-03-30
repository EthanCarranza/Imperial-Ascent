package com.ethan.byztine.domain.event;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.combat.CombatEngine;
import com.ethan.byztine.domain.combat.DefaultDamageCalculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CombatEventTest {

    @Test
    void higherIntelligenceShouldIncreaseCombatExperienceReward() {
        Character baseAttacker = createWinningAttacker("Base");
        Character smartAttacker = createWinningAttacker("Smart");
        Character baseOpponent = new Character("Enemy One");
        Character smartOpponent = new Character("Enemy Two");

        smartAttacker.getStats().increaseIntelligence(3);

        CombatEngine engine = new CombatEngine(
                new DefaultDamageCalculator(bound -> 2));

        EventResult baseResult = new CombatEvent(engine, baseOpponent).execute(baseAttacker);
        EventResult smartResult = new CombatEvent(engine, smartOpponent).execute(smartAttacker);

        assertEquals(100, baseResult.getExperienceGained());
        assertEquals(106, smartResult.getExperienceGained());
    }

    @Test
    void higherLuckShouldIncreaseCombatGoldReward() {
        Character baseAttacker = createWinningAttacker("Base Gold");
        Character luckyAttacker = createWinningAttacker("Lucky Gold");
        Character baseOpponent = new Character("Enemy Three");
        Character luckyOpponent = new Character("Enemy Four");

        luckyAttacker.getStats().increaseLuck(3);

        CombatEngine engine = new CombatEngine(
                new DefaultDamageCalculator(bound -> 2));

        EventResult baseResult = new CombatEvent(engine, baseOpponent).execute(baseAttacker);
        EventResult luckyResult = new CombatEvent(engine, luckyOpponent).execute(luckyAttacker);

        assertEquals(50, baseResult.getGoldGained());
        assertEquals(53, luckyResult.getGoldGained());
    }

    private Character createWinningAttacker(String name) {
        Character character = new Character(name);
        character.gainExperience(300);
        return character;
    }
}
