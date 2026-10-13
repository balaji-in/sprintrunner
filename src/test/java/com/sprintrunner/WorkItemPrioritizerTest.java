package com.sprintrunner;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class WorkItemPrioritizerTest {
    private final WorkItemPrioritizer prioritizer = new WorkItemPrioritizer();

    @Test void calculatesWeightedScore() { assertEquals(6, prioritizer.score(10, 5, 3, 3), 0.001); }
    @Test void calculatesCostOfDelay() { assertEquals(18, prioritizer.costOfDelay(10, 5, 3)); }
    @Test void assignsPriorityBands() {
        assertEquals("LOW", prioritizer.band(1)); assertEquals("MEDIUM", prioritizer.band(2));
        assertEquals("HIGH", prioritizer.band(5)); assertEquals("EXPEDITE", prioritizer.band(10));
    }
    @Test void identifiesUrgency() { assertTrue(prioritizer.isUrgent(8)); assertFalse(prioritizer.isUrgent(7)); }
    @Test void normalizesScore() { assertEquals(75, prioritizer.normalizedScore(15, 20), 0.001); }
    @Test void comparesScores() { assertTrue(prioritizer.compare(8, 5) > 0); assertEquals(0, prioritizer.compare(5, 5)); }
    @Test void selectsHigherScore() { assertEquals(8, prioritizer.higher(8, 5), 0.001); }
    @Test void calculatesEffortEfficiency() { assertEquals(4, prioritizer.effortEfficiency(12, 3), 0.001); }
    @Test void appliesDependencyPenalty() { assertEquals(5, prioritizer.applyDependencyPenalty(15, 2), 0.001); assertTrue(prioritizer.shouldExpedite(11)); }
    @Test void validatesInputs() {
        assertThrows(IllegalArgumentException.class, () -> prioritizer.score(-1, 1, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> prioritizer.score(1, 1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> prioritizer.band(-1));
        assertThrows(IllegalArgumentException.class, () -> prioritizer.normalizedScore(11, 10));
        assertThrows(IllegalArgumentException.class, () -> prioritizer.applyDependencyPenalty(1, -1));
    }
}

