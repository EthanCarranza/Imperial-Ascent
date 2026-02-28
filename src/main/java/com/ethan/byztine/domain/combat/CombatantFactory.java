package com.ethan.byztine.domain.combat;

import com.ethan.byztine.domain.Character;

public class CombatantFactory {

    private static final int BASE_ATTACK = 5;
    private static final int BASE_DEFENSE = 3;
    private static final int BASE_HEALTH = 20;

    public static Combatant fromCharacter(Character character) {

        int level = character.getLevel().getCurrentLevel();

        int attack = BASE_ATTACK + level;
        int defense = BASE_DEFENSE + level;
        int health = BASE_HEALTH + (level * 5);

        return new Combatant(attack, defense, health);
    }
}