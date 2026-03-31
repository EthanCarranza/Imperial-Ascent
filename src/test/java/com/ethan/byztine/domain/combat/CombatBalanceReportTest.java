package com.ethan.byztine.domain.combat;

import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.event.CombatEvent;
import com.ethan.byztine.domain.event.EventResult;
import com.ethan.byztine.domain.event.TrainingEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatBalanceReportTest {

    private static final int FIGHTS_PER_SIDE = 1_000;
    private static final long BASE_SEED = 42L;

    @Test
    void shouldProduceDeterministicBalanceSnapshot() {
        FighterSpec baseSpec = new FighterSpec("Base", 1, 5, 5, 5, 5);
        FighterSpec recruitSpec = fromPreset(EnemyPreset.RECRUIT);
        FighterSpec skirmisherSpec = fromPreset(EnemyPreset.SKIRMISHER);
        FighterSpec bruteSpec = fromPreset(EnemyPreset.BRUTE);
        FighterSpec duelistSpec = fromPreset(EnemyPreset.DUELIST);
        FighterSpec gamblerSpec = fromPreset(EnemyPreset.GAMBLER);
        FighterSpec veteranSpec = fromPreset(EnemyPreset.VETERAN);
        FighterSpec strengthSpec = new FighterSpec("Strength", 1, 8, 5, 5, 5);
        FighterSpec agilitySpec = new FighterSpec("Agility", 1, 5, 5, 8, 5);
        FighterSpec intelligenceSpec = new FighterSpec("Intelligence", 1, 5, 8, 5, 5);
        FighterSpec luckSpec = new FighterSpec("Luck", 1, 5, 5, 5, 8);

        MatchupReport baseVsBase = simulateMatchup(
                "Base vs Base",
                baseSpec,
                new FighterSpec("Base Mirror", 1, 5, 5, 5, 5),
                BASE_SEED);

        MatchupReport strengthVsBase = simulateMatchup(
                "STR +3 vs Base",
                strengthSpec,
                baseSpec,
                BASE_SEED);

        MatchupReport agilityVsBase = simulateMatchup(
                "AGI +3 vs Base",
                agilitySpec,
                baseSpec,
                BASE_SEED);

        MatchupReport intelligenceVsBase = simulateMatchup(
                "INT +3 vs Base",
                intelligenceSpec,
                baseSpec,
                BASE_SEED);

        MatchupReport luckVsBase = simulateMatchup(
                "LUCK +3 vs Base",
                luckSpec,
                baseSpec,
                BASE_SEED);

        MatchupReport allStatsPlus10VsBase = simulateMatchup(
                "ALL +10 vs Base",
                new FighterSpec("Omni Ten", 1, 15, 15, 15, 15),
                new FighterSpec("Base", 1, 5, 5, 5, 5),
                BASE_SEED);

        MatchupReport allStatsPlus20VsBase = simulateMatchup(
                "ALL +20 vs Base",
                new FighterSpec("Omni Twenty", 1, 25, 25, 25, 25),
                baseSpec,
                BASE_SEED);

        MatchupReport baseVsRecruit = simulateMatchup(
                "Base vs Recruit",
                baseSpec,
                recruitSpec,
                BASE_SEED);

        MatchupReport baseVsBrute = simulateMatchup(
                "Base vs Brute",
                baseSpec,
                bruteSpec,
                BASE_SEED);

        MatchupReport baseVsSkirmisher = simulateMatchup(
                "Base vs Skirmisher",
                baseSpec,
                skirmisherSpec,
                BASE_SEED);

        MatchupReport baseVsDuelist = simulateMatchup(
                "Base vs Duelist",
                baseSpec,
                duelistSpec,
                BASE_SEED);

        MatchupReport baseVsGambler = simulateMatchup(
                "Base vs Gambler",
                baseSpec,
                gamblerSpec,
                BASE_SEED);

        MatchupReport baseVsVeteran = simulateMatchup(
                "Base vs Veteran",
                baseSpec,
                veteranSpec,
                BASE_SEED);

        MatchupReport bruteVsDuelist = simulateMatchup(
                "Brute vs Duelist",
                bruteSpec,
                duelistSpec,
                BASE_SEED);

        MatchupReport bruteVsGambler = simulateMatchup(
                "Brute vs Gambler",
                bruteSpec,
                gamblerSpec,
                BASE_SEED);

        MatchupReport duelistVsGambler = simulateMatchup(
                "Duelist vs Gambler",
                duelistSpec,
                gamblerSpec,
                BASE_SEED);

        MatchupReport strengthVsDuelist = simulateMatchup(
                "STR +3 vs Duelist",
                strengthSpec,
                duelistSpec,
                BASE_SEED);

        MatchupReport agilityVsBrute = simulateMatchup(
                "AGI +3 vs Brute",
                agilitySpec,
                bruteSpec,
                BASE_SEED);

        ExperienceReport baseExperience = simulateExperienceProgression(
                new FighterSpec("Base XP", 5, 5, 5, 5, 5));
        ExperienceReport intelligenceExperience = simulateExperienceProgression(
                new FighterSpec("Smart XP", 5, 5, 8, 5, 5));
        GoldReport baseGold = simulateCombatGoldReward(
                new FighterSpec("Base Gold", 5, 5, 5, 5, 5));
        GoldReport luckyGold = simulateCombatGoldReward(
                new FighterSpec("Lucky Gold", 5, 5, 5, 5, 8));

        printReports("Core Stat Snapshot", List.of(
                baseVsBase,
                strengthVsBase,
                agilityVsBase,
                intelligenceVsBase,
                luckVsBase,
                allStatsPlus10VsBase,
                allStatsPlus20VsBase));
        printReports("Preset Matchups", List.of(
                baseVsRecruit,
                baseVsSkirmisher,
                baseVsBrute,
                baseVsDuelist,
                baseVsGambler,
                baseVsVeteran,
                bruteVsDuelist,
                bruteVsGambler,
                duelistVsGambler,
                strengthVsDuelist,
                agilityVsBrute));
        printExperienceReports(baseExperience, intelligenceExperience);
        printGoldReports(baseGold, luckyGold);

        assertTrue(baseVsBase.leftWinRate() > 0.45 && baseVsBase.leftWinRate() < 0.55);
        assertTrue(strengthVsBase.leftWinRate() > baseVsBase.leftWinRate());
        assertTrue(strengthVsBase.leftWinRate() < 0.80);
        assertTrue(agilityVsBase.leftWinRate() > baseVsBase.leftWinRate());
        assertTrue(agilityVsBase.leftWinRate() < 0.80);
        assertEquals(baseVsBase.leftWinRate(), intelligenceVsBase.leftWinRate());
        assertTrue(luckVsBase.leftWinRate() > baseVsBase.leftWinRate());
        assertTrue(luckVsBase.leftWinRate() < 0.70);
        assertTrue(allStatsPlus10VsBase.leftWinRate() > luckVsBase.leftWinRate());
        assertTrue(allStatsPlus20VsBase.leftWinRate() > allStatsPlus10VsBase.leftWinRate());
        assertTrue(allStatsPlus20VsBase.leftWinRate() > 0.90);
        assertTrue(baseVsRecruit.leftWinRate() > 0.45 && baseVsRecruit.leftWinRate() < 0.55);
        assertTrue(baseVsSkirmisher.rightWinRate() > baseVsRecruit.rightWinRate());
        assertTrue(baseVsVeteran.rightWinRate() > 0.60);

        assertEquals(100, baseExperience.combatXp());
        assertEquals(106, intelligenceExperience.combatXp());
        assertEquals(20, baseExperience.trainingXp());
        assertEquals(23, intelligenceExperience.trainingXp());
        assertEquals(6.0, intelligenceExperience.combatXpBonusPercent());
        assertEquals(15.0, intelligenceExperience.trainingXpBonusPercent());
        assertEquals(50, baseGold.combatGold());
        assertEquals(53, luckyGold.combatGold());
        assertEquals(6.0, luckyGold.combatGoldBonusPercent());
    }

    private MatchupReport simulateMatchup(
            String label,
            FighterSpec left,
            FighterSpec right,
            long seed) {

        SeriesReport leftAttacking = simulateSeries(left, right, seed);
        SeriesReport rightAttacking = simulateSeries(right, left, seed + 1);

        int totalFights = leftAttacking.totalFights() + rightAttacking.totalFights();
        int draws = leftAttacking.draws() + rightAttacking.draws();

        int leftWins = leftAttacking.attackerWins() + rightAttacking.defenderWins();
        int rightWins = leftAttacking.defenderWins() + rightAttacking.attackerWins();

        double averageRounds = (leftAttacking.totalRounds() + rightAttacking.totalRounds())
                / (double) totalFights;

        return new MatchupReport(
                label,
                leftWins / (double) totalFights,
                rightWins / (double) totalFights,
                draws / (double) totalFights,
                averageRounds);
    }

    private SeriesReport simulateSeries(FighterSpec attacker, FighterSpec defender, long seed) {
        CombatEngine engine = new CombatEngine(
                new DefaultDamageCalculator(new SeededRandomProvider(seed)));

        int attackerWins = 0;
        int defenderWins = 0;
        int draws = 0;
        int totalRounds = 0;

        for (int i = 0; i < FIGHTS_PER_SIDE; i++) {
            Combatant attackerCombatant = CombatantFactory.fromCharacter(buildCharacter(attacker));
            Combatant defenderCombatant = CombatantFactory.fromCharacter(buildCharacter(defender));

            CombatResult result = engine.fight(attackerCombatant, defenderCombatant);
            totalRounds += result.getRounds();

            if (result.isDraw()) {
                draws++;
            } else if (result.attackerWon()) {
                attackerWins++;
            } else {
                defenderWins++;
            }
        }

        return new SeriesReport(attackerWins, defenderWins, draws, FIGHTS_PER_SIDE, totalRounds);
    }

    private ExperienceReport simulateExperienceProgression(FighterSpec spec) {
        Character trainingCharacter = buildCharacter(spec);
        Character combatCharacter = buildCharacter(spec);
        Character opponent = buildCharacter(new FighterSpec("Opponent", 1, 5, 5, 5, 5));

        EventResult trainingResult = new TrainingEvent("str").execute(trainingCharacter);
        EventResult combatResult = new CombatEvent(
                new CombatEngine(new DefaultDamageCalculator(new SeededRandomProvider(BASE_SEED))),
                opponent)
                .execute(combatCharacter);

        double combatBonusPercent = calculateBonusPercent(100, combatResult.getExperienceGained());
        double trainingBonusPercent = calculateBonusPercent(20, trainingResult.getExperienceGained());

        return new ExperienceReport(
                spec.name(),
                combatResult.getExperienceGained(),
                trainingResult.getExperienceGained(),
                combatBonusPercent,
                trainingBonusPercent);
    }

    private GoldReport simulateCombatGoldReward(FighterSpec spec) {
        Character combatCharacter = buildCharacter(spec);
        Character opponent = buildCharacter(new FighterSpec("Opponent", 1, 5, 5, 5, 5));

        EventResult combatResult = new CombatEvent(
                new CombatEngine(new DefaultDamageCalculator(new SeededRandomProvider(BASE_SEED))),
                opponent)
                .execute(combatCharacter);

        double combatGoldBonusPercent = calculateBonusPercent(50, combatResult.getGoldGained());

        return new GoldReport(
                spec.name(),
                combatResult.getGoldGained(),
                combatGoldBonusPercent);
    }

    private double calculateBonusPercent(int baseValue, int actualValue) {
        return ((actualValue - baseValue) * 100.0) / baseValue;
    }

    private Character buildCharacter(FighterSpec spec) {
        Character character = new Character(spec.name());
        levelUpTo(character, spec.level());
        increaseStat(character, spec.strength(), StatType.STRENGTH);
        increaseStat(character, spec.intelligence(), StatType.INTELLIGENCE);
        increaseStat(character, spec.agility(), StatType.AGILITY);
        increaseStat(character, spec.luck(), StatType.LUCK);
        return character;
    }

    private void levelUpTo(Character character, int targetLevel) {
        if (targetLevel < 1) {
            throw new IllegalArgumentException("Level must be at least 1");
        }

        if (targetLevel == 1) {
            return;
        }

        int totalExperience = 0;

        for (int level = 1; level < targetLevel; level++) {
            totalExperience += 100 * level;
        }

        character.gainExperience(totalExperience);
    }

    private void increaseStat(Character character, int targetValue, StatType type) {
        int currentValue = switch (type) {
            case STRENGTH -> character.getStats().getStrength();
            case INTELLIGENCE -> character.getStats().getIntelligence();
            case AGILITY -> character.getStats().getAgility();
            case LUCK -> character.getStats().getLuck();
        };

        if (targetValue < currentValue) {
            throw new IllegalArgumentException("Balance harness does not support lowering stats below base");
        }

        int amount = targetValue - currentValue;

        if (amount == 0) {
            return;
        }

        switch (type) {
            case STRENGTH -> character.getStats().increaseStrength(amount);
            case INTELLIGENCE -> character.getStats().increaseIntelligence(amount);
            case AGILITY -> character.getStats().increaseAgility(amount);
            case LUCK -> character.getStats().increaseLuck(amount);
        }
    }

    private FighterSpec fromPreset(EnemyPreset preset) {
        return new FighterSpec(
                preset.getDisplayName(),
                preset.getLevel(),
                preset.getStrength(),
                preset.getIntelligence(),
                preset.getAgility(),
                preset.getLuck());
    }

    private void printReports(String title, List<MatchupReport> reports) {
        System.out.println("\n=== " + title + " ===");
        reports.forEach(report -> System.out.printf(
                "%s -> left %.2f%% | right %.2f%% | draws %.2f%% | avg rounds %.2f%n",
                report.label(),
                report.leftWinRate() * 100,
                report.rightWinRate() * 100,
                report.drawRate() * 100,
                report.averageRounds()));
    }

    private void printExperienceReports(ExperienceReport base, ExperienceReport intelligence) {
        System.out.println("\n=== Experience Snapshot ===");
        System.out.printf(
                "%s -> combat XP %d | training XP %d%n",
                base.label(),
                base.combatXp(),
                base.trainingXp());
        System.out.printf(
                "%s -> combat XP %d (%.2f%%) | training XP %d (%.2f%%)%n",
                intelligence.label(),
                intelligence.combatXp(),
                intelligence.combatXpBonusPercent(),
                intelligence.trainingXp(),
                intelligence.trainingXpBonusPercent());
    }

    private void printGoldReports(GoldReport base, GoldReport lucky) {
        System.out.println("\n=== Gold Snapshot ===");
        System.out.printf(
                "%s -> combat gold %d%n",
                base.label(),
                base.combatGold());
        System.out.printf(
                "%s -> combat gold %d (%.2f%%)%n",
                lucky.label(),
                lucky.combatGold(),
                lucky.combatGoldBonusPercent());
    }

    private record FighterSpec(
            String name,
            int level,
            int strength,
            int intelligence,
            int agility,
            int luck) {
    }

    private record SeriesReport(
            int attackerWins,
            int defenderWins,
            int draws,
            int totalFights,
            int totalRounds) {
    }

    private record MatchupReport(
            String label,
            double leftWinRate,
            double rightWinRate,
            double drawRate,
            double averageRounds) {
    }

    private record ExperienceReport(
            String label,
            int combatXp,
            int trainingXp,
            double combatXpBonusPercent,
            double trainingXpBonusPercent) {
    }

    private record GoldReport(
            String label,
            int combatGold,
            double combatGoldBonusPercent) {
    }

    private enum StatType {
        STRENGTH,
        INTELLIGENCE,
        AGILITY,
        LUCK
    }

    private static final class SeededRandomProvider implements RandomProvider {

        private final Random random;

        private SeededRandomProvider(long seed) {
            this.random = new Random(seed);
        }

        @Override
        public int nextInt(int bound) {
            return random.nextInt(bound);
        }
    }
}
