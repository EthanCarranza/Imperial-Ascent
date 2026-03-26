package com.ethan.byztine.domain.combat;

import org.springframework.stereotype.Component;

@Component
public class CombatEngine {

    private static final int MAX_ROUNDS = 100;

    private final DamageCalculator damageCalculator;

    public CombatEngine(DamageCalculator damageCalculator) {
        this.damageCalculator = damageCalculator;
    }

    public CombatResult fight(Combatant attacker, Combatant defender) {

        int rounds = 0;
        boolean attackerTurn = true;

        while (attacker.isAlive() && defender.isAlive() && rounds < MAX_ROUNDS) {

            if (attackerTurn) {
                int damage = damageCalculator.calculate(attacker, defender);
                defender.receiveDamage(damage);
            } else {
                int damage = damageCalculator.calculate(defender, attacker);
                attacker.receiveDamage(damage);
            }

            attackerTurn = !attackerTurn;
            rounds++;
        }

        boolean attackerWon = attacker.isAlive() && !defender.isAlive();
        boolean draw = rounds >= MAX_ROUNDS && attacker.isAlive() && defender.isAlive();

        return new CombatResult(attackerWon, rounds, draw);
    }
}