package com.ethan.byztine.domain.combat;

class FixedRandomProvider implements RandomProvider {

    @Override
    public int nextInt(int bound) {
        return 2; // siempre devuelve el centro
    }
}