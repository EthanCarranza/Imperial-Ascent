package com.ethan.byztine.domain.exploration;

import com.ethan.byztine.domain.Character;

public final class ExplorationEnemyFactory {

    private ExplorationEnemyFactory() {
    }

    public static Character create(ExplorationEncounter encounter) {
        Character enemy = new Character(encounter.getDisplayName());
        levelUpTo(enemy, encounter.getLevel());
        increaseStrengthTo(enemy, encounter.getStrength());
        increaseIntelligenceTo(enemy, encounter.getIntelligence());
        increaseAgilityTo(enemy, encounter.getAgility());
        increaseLuckTo(enemy, encounter.getLuck());
        return enemy;
    }

    private static void levelUpTo(Character character, int targetLevel) {
        if (targetLevel <= 1) {
            return;
        }

        int totalExperience = 0;

        for (int level = 1; level < targetLevel; level++) {
            totalExperience += 100 * level;
        }

        character.gainExperience(totalExperience);
    }

    private static void increaseStrengthTo(Character character, int targetValue) {
        int amount = targetValue - character.getStats().getStrength();

        if (amount > 0) {
            character.getStats().increaseStrength(amount);
        }
    }

    private static void increaseIntelligenceTo(Character character, int targetValue) {
        int amount = targetValue - character.getStats().getIntelligence();

        if (amount > 0) {
            character.getStats().increaseIntelligence(amount);
        }
    }

    private static void increaseAgilityTo(Character character, int targetValue) {
        int amount = targetValue - character.getStats().getAgility();

        if (amount > 0) {
            character.getStats().increaseAgility(amount);
        }
    }

    private static void increaseLuckTo(Character character, int targetValue) {
        int amount = targetValue - character.getStats().getLuck();

        if (amount > 0) {
            character.getStats().increaseLuck(amount);
        }
    }
}
