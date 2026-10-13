package com.sprintrunner;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class BurndownCalculatorTest {
    private BurndownCalculator burndown() { return new BurndownCalculator(40, 10); }

    @Test void calculatesRemainingPoints() { assertEquals(25, burndown().remaining(15)); }
    @Test void reportsPointsBurned() { assertEquals(15, burndown().pointsBurned(15)); }
    @Test void calculatesCompletionPercent() { assertEquals(25, burndown().percentComplete(10), 0.001); }
    @Test void calculatesIdealRemaining() { assertEquals(20, burndown().idealRemaining(5), 0.001); }
    @Test void calculatesDailyBurnRate() { assertEquals(4, burndown().dailyBurnRate(20, 5), 0.001); }
    @Test void calculatesRequiredRate() { assertEquals(4, burndown().requiredDailyRate(20, 5), 0.001); }
    @Test void detectsAheadAndBehind() { assertTrue(burndown().isAhead(24, 5)); assertFalse(burndown().isAhead(10, 5)); }
    @Test void projectsCompletion() { assertEquals(10, burndown().projectedCompletionDays(20, 5)); assertEquals(Integer.MAX_VALUE, burndown().projectedCompletionDays(0, 2)); }
    @Test void reportsCompletionAndDaysRemaining() { assertTrue(burndown().isComplete(40)); assertEquals(3, burndown().daysRemaining(7)); }
    @Test void validatesInputs() {
        assertThrows(IllegalArgumentException.class, () -> new BurndownCalculator(0, 10));
        assertThrows(IllegalArgumentException.class, () -> burndown().remaining(41));
        assertThrows(IllegalArgumentException.class, () -> burndown().idealRemaining(11));
        assertThrows(IllegalArgumentException.class, () -> burndown().dailyBurnRate(1, 0));
        assertThrows(IllegalArgumentException.class, () -> burndown().daysRemaining(-1));
    }
}

