package com.sprintrunner;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class ReleasePlannerTest {
    private ReleasePlanner planner() { return new ReleasePlanner(20, 10); }

    @Test void calculatesRequiredSprints() { assertEquals(3, planner().sprintsRequired(41)); assertEquals(0, planner().sprintsRequired(0)); }
    @Test void forecastsCalendarDays() { assertEquals(30, planner().forecastDays(41)); }
    @Test void calculatesCapacity() { assertEquals(60, planner().totalCapacity(3)); }
    @Test void evaluatesDeliveryFit() { assertTrue(planner().canDeliver(40, 2)); assertFalse(planner().canDeliver(41, 2)); }
    @Test void calculatesRemainingPoints() { assertEquals(15, planner().remainingPoints(55, 2)); assertEquals(0, planner().remainingPoints(20, 2)); }
    @Test void calculatesUtilization() { assertEquals(75, planner().utilizationPercent(30, 2), 0.001); assertEquals(0, planner().utilizationPercent(0, 0), 0.001); }
    @Test void calculatesBuffer() { assertEquals(10, planner().bufferPoints(30, 2)); assertEquals(0, planner().bufferPoints(50, 2)); }
    @Test void identifiesRisk() { assertTrue(planner().isAtRisk(50, 2)); assertFalse(planner().isAtRisk(40, 2)); }
    @Test void distributesWork() { assertEquals(Arrays.asList(20, 20, 5), planner().distribute(45)); assertEquals(Collections.emptyList(), planner().distribute(0)); }
    @Test void validatesAndExposesConfiguration() {
        assertEquals(20, planner().getVelocity()); assertEquals(10, planner().getSprintLengthDays());
        assertThrows(IllegalArgumentException.class, () -> new ReleasePlanner(0, 10));
        assertThrows(IllegalArgumentException.class, () -> planner().sprintsRequired(-1));
        assertThrows(IllegalArgumentException.class, () -> planner().totalCapacity(-1));
    }
}

