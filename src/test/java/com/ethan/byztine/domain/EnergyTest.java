package com.ethan.byztine.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class EnergyTest {
    @Test
    void energyShouldStartFull() {
        Energy energy = new Energy(10);

        assertEquals(10, energy.getCurrentEnergy());
        assertEquals(10, energy.getMaxEnergy());
    }

    @Test
    void shouldConsumeOneEnergy() {
        Energy energy = new Energy(10);

        energy.consume(1);

        assertEquals(9, energy.getCurrentEnergy());
    }

    @Test
    void shouldNotAllowEnergyToGoNegative() {
        Energy energy = new Energy(1);

        energy.consume(1);

        assertThrows(IllegalStateException.class, () -> energy.consume(1));
    }

    @Test
    void shouldRegenerateEnergy() {
        Energy energy = new Energy(10);

        energy.consume(5);
        energy.regenerate(3);

        assertEquals(8, energy.getCurrentEnergy());
    }

    @Test
    void shouldNotExceedMaxEnergy() {
        Energy energy = new Energy(10);

        energy.consume(2);
        energy.regenerate(5);

        assertEquals(10, energy.getCurrentEnergy());
    }

    @Test
    void shouldNotAllowNegativeRegeneration() {
        Energy energy = new Energy(10);

        assertThrows(IllegalArgumentException.class, () -> energy.regenerate(-1));
    }

    @Test
    void shouldUpdateMaxEnergyAndRefillWhenMaxIncreases() {
        Energy energy = new Energy(10);

        energy.consume(5); // queda en 5

        energy.updateMaxEnergy(11);

        assertEquals(11, energy.getMaxEnergy());
        assertEquals(11, energy.getCurrentEnergy());
    }

}
