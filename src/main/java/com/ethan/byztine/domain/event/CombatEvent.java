package com.ethan.byztine.domain.event;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.combat.*;

public class CombatEvent implements GameEvent {

    private final CombatEngine engine;
    private final Character opponent;

    private static final int ENERGY_COST = 3;

    public CombatEvent(CombatEngine engine, Character opponent) {
        this.engine = engine;
        this.opponent = opponent;
    }

    @Override
    public EventResult execute(Character character) {

        int levelBefore = character.getLevel().getCurrentLevel();

        character.getEnergy().consume(ENERGY_COST);

        Combatant player = CombatantFactory.fromCharacter(character);
        Combatant enemy = CombatantFactory.fromCharacter(opponent);

        CombatResult result = engine.fight(player, enemy);

        int expGain = 0;
        int goldGain = 0;

        if (result.attackerWon()) {
            expGain = 100;
            goldGain = 50;
            character.gainExperience(expGain);
            character.addGold(goldGain);
        }

        int levelAfter = character.getLevel().getCurrentLevel();

        return new EventResult(
                ENERGY_COST,
                expGain,
                levelBefore,
                levelAfter,
                goldGain);
    }
}