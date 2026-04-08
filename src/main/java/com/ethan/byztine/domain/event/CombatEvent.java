package com.ethan.byztine.domain.event;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.StatScaling;
import com.ethan.byztine.domain.combat.*;

public class CombatEvent implements GameEvent {

    private final CombatEngine engine;
    private final Character opponent;

    private static final int ENERGY_COST = 3;
    private static final int BASE_XP_REWARD = 100;
    private static final int GOLD_REWARD = 50;
    private static final int BASE_INTELLIGENCE = 5;
    private static final double XP_BONUS_PER_INTELLIGENCE = 3.5;
    private static final int BASE_LUCK = 5;
    private static final double GOLD_BONUS_PER_LUCK_POINT = 0.035;

    public CombatEvent(CombatEngine engine, Character opponent) {
        this.engine = engine;
        this.opponent = opponent;
    }

    @Override
    public EventResult execute(Character character) {

        int levelBefore = character.getCurrentLevel();

        character.getEnergy().consume(ENERGY_COST);

        Combatant player = CombatantFactory.fromCharacter(character);
        Combatant enemy = CombatantFactory.fromCharacter(opponent);

        CombatReport report = engine.fightDetailed(player, enemy);
        CombatResult result = report.getResult();

        int expGain = 0;
        int goldGain = 0;

        if (result.attackerWon()) {
            expGain = calculateExperienceReward(character);
            goldGain = calculateGoldReward(character);
            character.gainExperience(expGain);
            character.addGold(goldGain);
        }

        int levelAfter = character.getCurrentLevel();

        return new CombatEventResult(
                ENERGY_COST,
                expGain,
                levelBefore,
                levelAfter,
                goldGain,
                report);
    }

    private int calculateExperienceReward(Character character) {
        int intelligence = character.getEffectiveIntelligence();
        double effectiveIntelligenceBonus = StatScaling.positiveSoftBonus(
                intelligence,
                BASE_INTELLIGENCE);
        int intelligenceBonus = (int) Math.round(effectiveIntelligenceBonus * XP_BONUS_PER_INTELLIGENCE);

        return BASE_XP_REWARD + intelligenceBonus;
    }

    private int calculateGoldReward(Character character) {
        int luck = character.getEffectiveLuck();
        double effectiveLuckBonus = StatScaling.positiveSoftBonus(luck, BASE_LUCK);
        double goldMultiplier = 1.0 + (effectiveLuckBonus * GOLD_BONUS_PER_LUCK_POINT);

        return (int) Math.round(GOLD_REWARD * goldMultiplier);
    }
}
