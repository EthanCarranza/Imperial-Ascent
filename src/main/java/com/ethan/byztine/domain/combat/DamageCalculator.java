package com.ethan.byztine.domain.combat;

public interface DamageCalculator {
    int calculate(Combatant attacker, Combatant defender);
}
