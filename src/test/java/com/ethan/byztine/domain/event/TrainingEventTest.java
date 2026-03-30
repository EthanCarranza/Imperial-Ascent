package com.ethan.byztine.domain.event;

import com.ethan.byztine.domain.Character;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainingEventTest {

    @Test
    void higherIntelligenceShouldIncreaseTrainingExperienceReward() {
        Character base = new Character("Base");
        Character smart = new Character("Smart");

        smart.getStats().increaseIntelligence(3);

        EventResult baseResult = new TrainingEvent("str").execute(base);
        EventResult smartResult = new TrainingEvent("str").execute(smart);

        assertEquals(20, baseResult.getExperienceGained());
        assertEquals(23, smartResult.getExperienceGained());
    }

    @Test
    void luckTrainingShouldIncreaseLuck() {
        Character lucky = new Character("Lucky");

        new TrainingEvent("luck").execute(lucky);

        assertEquals(6, lucky.getStats().getLuck());
    }
}
