package com.ethan.byztine.domain.combat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class CombatEngine {

    private static final int MAX_ROUNDS = 20;

    private final DamageCalculator damageCalculator;

    public CombatEngine(DamageCalculator damageCalculator) {
        this.damageCalculator = damageCalculator;
    }

    public CombatResult fight(Combatant attacker, Combatant defender) {
        return fightDetailed(attacker, defender).getResult();
    }

    public CombatReport fightDetailed(Combatant attacker, Combatant defender) {

        int rounds = 0;
        List<CombatRoundReport> roundReports = new ArrayList<>();

        while (attacker.isAlive() && defender.isAlive() && rounds < MAX_ROUNDS) {
            rounds++;

            int attackerDamage = damageCalculator.calculate(attacker, defender);
            defender.receiveDamage(attackerDamage);

            int defenderDamage = 0;
            boolean defenderActed = false;

            if (defender.isAlive()) {
                defenderDamage = damageCalculator.calculate(defender, attacker);
                attacker.receiveDamage(defenderDamage);
                defenderActed = true;
            }

            roundReports.add(new CombatRoundReport(
                    rounds,
                    attackerDamage,
                    defenderDamage,
                    attacker.getCurrentHealth(),
                    defender.getCurrentHealth(),
                    defenderActed));
        }

        boolean reachedRoundLimit = rounds >= MAX_ROUNDS && attacker.isAlive() && defender.isAlive();
        CombatResult result = buildResult(attacker, defender, rounds, reachedRoundLimit);

        return new CombatReport(
                result,
                roundReports,
                attacker.getCurrentHealth(),
                defender.getCurrentHealth(),
                reachedRoundLimit);
    }

    private CombatResult buildResult(
            Combatant attacker,
            Combatant defender,
            int rounds,
            boolean reachedRoundLimit) {

        if (!attacker.isAlive()) {
            return new CombatResult(false, rounds, false);
        }

        if (!defender.isAlive()) {
            return new CombatResult(true, rounds, false);
        }

        if (reachedRoundLimit) {
            if (attacker.getCurrentHealth() > defender.getCurrentHealth()) {
                return new CombatResult(true, rounds, false);
            }

            if (defender.getCurrentHealth() > attacker.getCurrentHealth()) {
                return new CombatResult(false, rounds, false);
            }
        }

        return new CombatResult(false, rounds, true);
    }
}
