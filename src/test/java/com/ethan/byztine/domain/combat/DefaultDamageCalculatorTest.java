package com.ethan.byztine.domain.combat;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultDamageCalculatorTest {

    @Test
    void higherAgilityShouldTurnTheSameRollIntoAHit() {
        Combatant fastAttacker = new Combatant(10, 3, 20, 8);
        Combatant slowAttacker = new Combatant(10, 3, 20, 5);
        Combatant defender = new Combatant(5, 3, 20, 8);

        DamageCalculator fastCalculator = new DefaultDamageCalculator(
                new SequenceRandomProvider(74, 2, 99));
        DamageCalculator slowCalculator = new DefaultDamageCalculator(
                new SequenceRandomProvider(74));

        int fastDamage = fastCalculator.calculate(fastAttacker, defender);
        int slowDamage = slowCalculator.calculate(slowAttacker, defender);

        assertTrue(fastDamage > 0);
        assertEquals(0, slowDamage);
    }

    @Test
    void higherStrengthShouldDealMoreDamageWithTheSameRoll() {
        Combatant strongerAttacker = new Combatant(10, 3, 20, 8, 5);
        Combatant weakerAttacker = new Combatant(10, 3, 20, 5, 5);
        Combatant defender = new Combatant(5, 3, 20, 5);

        DamageCalculator calculator = new DefaultDamageCalculator(
                new SequenceRandomProvider(0, 2, 99, 0, 2, 99));

        int strongDamage = calculator.calculate(strongerAttacker, defender);
        int weakDamage = calculator.calculate(weakerAttacker, defender);

        assertTrue(strongDamage > weakDamage);
    }

    @Test
    void higherLuckShouldOccasionallyShiftDamageVariationInAttackersFavor() {
        Combatant luckyAttacker = new Combatant(10, 3, 20, 5, 5, 8);
        Combatant normalAttacker = new Combatant(10, 3, 20, 5, 5, 5);
        Combatant defender = new Combatant(5, 3, 20, 5, 5, 5);

        DamageCalculator luckyCalculator = new DefaultDamageCalculator(
                new SequenceRandomProvider(0, 2, 0, 99));
        DamageCalculator normalCalculator = new DefaultDamageCalculator(
                new SequenceRandomProvider(0, 2, 99));

        int luckyDamage = luckyCalculator.calculate(luckyAttacker, defender);
        int normalDamage = normalCalculator.calculate(normalAttacker, defender);

        assertTrue(luckyDamage > normalDamage);
    }

    @Test
    void higherLuckShouldIncreaseCriticalChanceOnTheSameRoll() {
        Combatant luckyAttacker = new Combatant(10, 3, 20, 5, 5, 8);
        Combatant normalAttacker = new Combatant(10, 3, 20, 5, 5, 5);
        Combatant defender = new Combatant(5, 3, 20, 5, 5, 5);

        DamageCalculator luckyCalculator = new DefaultDamageCalculator(
                new SequenceRandomProvider(0, 2, 99, 5));
        DamageCalculator normalCalculator = new DefaultDamageCalculator(
                new SequenceRandomProvider(0, 2, 5));

        int luckyDamage = luckyCalculator.calculate(luckyAttacker, defender);
        int normalDamage = normalCalculator.calculate(normalAttacker, defender);

        assertTrue(luckyDamage > normalDamage);
    }

    private static final class SequenceRandomProvider implements RandomProvider {

        private final Queue<Integer> values = new ArrayDeque<>();

        private SequenceRandomProvider(int... values) {
            for (int value : values) {
                this.values.add(value);
            }
        }

        @Override
        public int nextInt(int bound) {
            if (values.isEmpty()) {
                throw new IllegalStateException("No more fixed random values");
            }

            return values.remove();
        }
    }
}
