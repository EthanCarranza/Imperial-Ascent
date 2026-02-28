package com.ethan.byztine.domain.combat;

public class CombatResult {

    private final boolean attackerWon;
    private final int rounds;
    private final boolean draw;

    public CombatResult(boolean attackerWon, int rounds, boolean draw) {
        this.attackerWon = attackerWon;
        this.rounds = rounds;
        this.draw = draw;
    }

    public boolean attackerWon() {
        return attackerWon;
    }

    public int getRounds() {
        return rounds;
    }

    public boolean isDraw() {
        return draw;
    }
}