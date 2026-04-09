package com.ethan.byztine.domain.combat;

import com.ethan.byztine.domain.StatScaling;
import org.springframework.stereotype.Component;

@Component
public class DefaultDamageCalculator implements DamageCalculator {

    private static final int BASE_HIT_CHANCE = 75;
    private static final double HIT_CHANCE_SWING = 8.0;
    private static final double HIT_CHANCE_CURVE_DIVISOR = 3.5;
    private static final int BASE_STRENGTH = 5;
    private static final double DAMAGE_MULTIPLIER_PER_STRENGTH_POINT = 0.08;
    private static final double DAMAGE_MULTIPLIER_PER_WEAPON_DAMAGE = 0.08;
    private static final double DAMAGE_REDUCTION_PER_ARMOR_POINT = 0.08;
    private static final double MIN_ARMOR_MULTIPLIER = 0.35;
    private static final int BASE_CRIT_CHANCE = 3;
    private static final double POSITIVE_CRIT_CHANCE_SWING = 7.0;
    private static final double NEGATIVE_CRIT_CHANCE_SWING = 2.0;
    private static final double CRIT_CHANCE_CURVE_DIVISOR = 3.5;
    private static final double CRIT_DAMAGE_MULTIPLIER = 1.5;
    private static final double MAX_LUCK_SWING_CHANCE = 24.0;
    private static final double LUCK_SWING_CURVE_DIVISOR = 3.5;
    private static final int MIN_DAMAGE_VARIATION = -2;
    private static final int MAX_DAMAGE_VARIATION = 2;

    private final RandomProvider randomProvider;

    public DefaultDamageCalculator(RandomProvider randomProvider) {
        this.randomProvider = randomProvider;
    }

    @Override
    public int calculate(Combatant attacker, Combatant defender) {

        int hitChance = calculateHitChance(attacker, defender);
        int hitRoll = randomProvider.nextInt(100);

        if (hitRoll >= hitChance) {
            return 0;
        }

        double baseDamage = attacker.getAttack() - defender.getDefense();
        int variation = randomProvider.nextInt(5) - 2;
        int luckAdjustedVariation = applyLuckSwing(variation, attacker, defender);

        double rawDamage = Math.max(1.0, baseDamage + luckAdjustedVariation);
        double strengthMultiplier = calculateStrengthMultiplier(attacker);
        double weaponMultiplier = calculateWeaponMultiplier(attacker);
        double armorMultiplier = calculateArmorMultiplier(defender);
        int finalDamage = (int) Math.round(rawDamage * strengthMultiplier * weaponMultiplier * armorMultiplier);
        int criticalDamage = applyCriticalHit(finalDamage, attacker, defender);

        return Math.max(1, criticalDamage);
    }

    private int calculateHitChance(Combatant attacker, Combatant defender) {
        double agilityDifference = StatScaling.signedSoftDifference(
                attacker.getAgility(),
                defender.getAgility());
        double hitChance = BASE_HIT_CHANCE
                + (HIT_CHANCE_SWING * Math.tanh(agilityDifference / HIT_CHANCE_CURVE_DIVISOR));

        return (int) Math.round(hitChance);
    }

    private double calculateStrengthMultiplier(Combatant attacker) {
        double strengthDifference = StatScaling.signedSoftDifference(
                attacker.getStrength(),
                BASE_STRENGTH);

        return Math.max(0.25, 1.0 + (strengthDifference * DAMAGE_MULTIPLIER_PER_STRENGTH_POINT));
    }

    private double calculateWeaponMultiplier(Combatant attacker) {
        return 1.0 + (attacker.getWeaponDamage() * DAMAGE_MULTIPLIER_PER_WEAPON_DAMAGE);
    }

    private double calculateArmorMultiplier(Combatant defender) {
        return Math.max(
                MIN_ARMOR_MULTIPLIER,
                1.0 - (defender.getArmor() * DAMAGE_REDUCTION_PER_ARMOR_POINT));
    }

    private int applyCriticalHit(int damage, Combatant attacker, Combatant defender) {
        int critChance = calculateCriticalChance(attacker, defender);
        int critRoll = randomProvider.nextInt(100);

        if (critRoll >= critChance) {
            return damage;
        }

        return (int) Math.round(damage * CRIT_DAMAGE_MULTIPLIER);
    }

    private int calculateCriticalChance(Combatant attacker, Combatant defender) {
        double luckDifference = calculateLuckDifference(attacker, defender);
        double critChance = BASE_CRIT_CHANCE;

        if (luckDifference >= 0) {
            critChance += POSITIVE_CRIT_CHANCE_SWING * Math.tanh(luckDifference / CRIT_CHANCE_CURVE_DIVISOR);
        } else {
            critChance -= NEGATIVE_CRIT_CHANCE_SWING * Math.tanh(-luckDifference / CRIT_CHANCE_CURVE_DIVISOR);
        }

        return (int) Math.round(critChance);
    }

    private int applyLuckSwing(int variation, Combatant attacker, Combatant defender) {
        double luckDifference = calculateLuckDifference(attacker, defender);

        if (luckDifference == 0) {
            return variation;
        }

        double swingChance = MAX_LUCK_SWING_CHANCE
                * Math.tanh(Math.abs(luckDifference) / LUCK_SWING_CURVE_DIVISOR);
        int luckRoll = randomProvider.nextInt(100);

        if (luckRoll >= swingChance) {
            return variation;
        }

        int shiftedVariation = variation + (luckDifference > 0 ? 1 : -1);

        return Math.max(MIN_DAMAGE_VARIATION, Math.min(MAX_DAMAGE_VARIATION, shiftedVariation));
    }

    private double calculateLuckDifference(Combatant attacker, Combatant defender) {
        return StatScaling.signedSoftDifference(attacker.getLuck(), defender.getLuck());
    }
}
