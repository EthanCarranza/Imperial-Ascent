package com.ethan.byztine.domain.event;

public abstract class BaseEvent implements GameEvent {

    private final int energyCost;

    protected BaseEvent() {
        this.energyCost = 1;
    }

    protected BaseEvent(int energyCost) {
        if (energyCost <= 0) {
            throw new IllegalArgumentException("Energy cost must be positive");
        }
        this.energyCost = energyCost;
    }

    @Override
    public int energyCost() {
        return energyCost;
    }
}