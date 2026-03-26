package com.ethan.byztine.domain.combat;

import org.springframework.stereotype.Component;

@Component
public class DefaultDamageCalculator implements DamageCalculator {

    private final RandomProvider randomProvider;

    public DefaultDamageCalculator(RandomProvider randomProvider) {
        this.randomProvider = randomProvider;
    }

    @Override
    public int calculate(Combatant attacker, Combatant defender) {

        int baseDamage = attacker.getAttack() - defender.getDefense();
        int variation = randomProvider.nextInt(5) - 2; // -2 to +2

        int finalDamage = baseDamage + variation;

        return Math.max(1, finalDamage);
    }
}
