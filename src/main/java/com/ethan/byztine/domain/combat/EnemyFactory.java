package com.ethan.byztine.domain.combat;

import com.ethan.byztine.domain.Character;

public final class EnemyFactory {

    private EnemyFactory() {
    }

    public static Character create(EnemyPreset preset) {
        Character enemy = new Character(preset.getDisplayName());
        levelUpTo(enemy, preset.getLevel());
        increaseStrengthTo(enemy, preset.getStrength());
        increaseIntelligenceTo(enemy, preset.getIntelligence());
        increaseAgilityTo(enemy, preset.getAgility());
        increaseLuckTo(enemy, preset.getLuck());
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
