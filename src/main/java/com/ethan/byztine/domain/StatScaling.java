package com.ethan.byztine.domain;

public final class StatScaling {

    private StatScaling() {
    }

    public static double positiveSoftBonus(int value, int baseValue) {
        return Math.sqrt(Math.max(0, value - baseValue));
    }

    public static double signedSoftDifference(int leftValue, int rightValue) {
        int difference = leftValue - rightValue;

        if (difference == 0) {
            return 0.0;
        }

        return Math.copySign(Math.sqrt(Math.abs(difference)), difference);
    }
}
