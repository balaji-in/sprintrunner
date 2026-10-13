package com.sprintrunner;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SprintCapacityTest {
    private SprintCapacity capacity() { return new SprintCapacity(4, 10, 8, 0.75); }

    @Test void calculatesTotalHours() { assertEquals(320, capacity().totalHours(), 0.001); }
    @Test void appliesFocusFactor() { assertEquals(240, capacity().focusedHours(), 0.001); }
    @Test void calculatesStoryPointCapacity() { assertEquals(30, capacity().storyPointCapacity(8)); }
    @Test void rejectsInvalidHoursPerPoint() {
        assertThrows(IllegalArgumentException.class, () -> capacity().storyPointCapacity(0));
    }
    @Test void reservesCapacity() {
        SprintCapacity capacity = capacity(); capacity.reserve(40); assertEquals(200, capacity.remainingHours(), 0.001);
    }
    @Test void rejectsExcessReservation() {
        assertThrows(IllegalArgumentException.class, () -> capacity().reserve(241));
        assertThrows(IllegalArgumentException.class, () -> capacity().reserve(0));
    }
    @Test void releasesReservedCapacity() {
        SprintCapacity capacity = capacity(); capacity.reserve(50); capacity.release(20);
        assertEquals(210, capacity.remainingHours(), 0.001);
    }
    @Test void reportsUtilization() {
        SprintCapacity capacity = capacity(); capacity.reserve(120); assertEquals(50, capacity.utilizationPercent(), 0.001);
    }
    @Test void identifiesFullAllocationAndReset() {
        SprintCapacity capacity = capacity(); capacity.reserve(240); assertTrue(capacity.isFullyAllocated());
        capacity.reset(); assertFalse(capacity.isFullyAllocated());
    }
    @Test void validatesConstructorAndRelease() {
        assertThrows(IllegalArgumentException.class, () -> new SprintCapacity(0, 1, 8, 1));
        assertThrows(IllegalArgumentException.class, () -> new SprintCapacity(1, 1, 8, 1.1));
        assertThrows(IllegalArgumentException.class, () -> capacity().release(1));
        assertTrue(capacity().canReserve(10)); assertFalse(capacity().canReserve(-1));
    }
}

