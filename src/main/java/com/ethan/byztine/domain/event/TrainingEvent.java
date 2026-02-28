package com.ethan.byztine.domain.event;

public class TrainingEvent extends BaseEvent {

    public TrainingEvent() {
        super();
    }

    @Override
    public int experienceReward() {
        return 50;
    }
}