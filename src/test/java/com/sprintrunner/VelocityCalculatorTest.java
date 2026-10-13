package com.sprintrunner;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class VelocityCalculatorTest {
    private VelocityCalculator calculator() { return new VelocityCalculator(Arrays.asList(20, 24, 22, 26)); }

    @Test void calculatesTotal() { assertEquals(92, calculator().total()); }
    @Test void calculatesAverage() { assertEquals(23, calculator().average(), 0.001); }
    @Test void calculatesEvenMedian() { assertEquals(23, calculator().median(), 0.001); }
    @Test void calculatesOddMedian() { assertEquals(7, new VelocityCalculator(Arrays.asList(9, 5, 7)).median(), 0.001); }
    @Test void findsMinimumAndMaximum() { assertEquals(20, calculator().minimum()); assertEquals(26, calculator().maximum()); }
    @Test void returnsLatestAndTrend() { assertEquals(26, calculator().latest()); assertEquals(6, calculator().trend()); }
    @Test void calculatesDeviation() { assertEquals(Math.sqrt(5), calculator().standardDeviation(), 0.001); }
    @Test void evaluatesStability() { assertTrue(calculator().isStable(3)); assertFalse(calculator().isStable(2)); }
    @Test void forecastsNextSprint() { assertEquals(25, calculator().forecastNext()); }
    @Test void validatesInput() {
        assertThrows(IllegalArgumentException.class, () -> new VelocityCalculator(Collections.emptyList()));
        assertThrows(IllegalArgumentException.class, () -> new VelocityCalculator(Arrays.asList(1, -1)));
        assertThrows(IllegalArgumentException.class, () -> calculator().isStable(-1));
    }
}

