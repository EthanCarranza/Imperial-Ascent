package com.ethan.byztine.domain.combat;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class DefaultRandomProvider implements RandomProvider {

    private final Random random = new Random();

    @Override
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }
}
