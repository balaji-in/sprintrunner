package com.sprintrunner;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class IterationMetricsTest {
    @Test void calculatesFinalScope() { assertEquals(45, new IterationMetrics(40, 35, 5, 2).finalScope()); }
    @Test void calculatesSpillover() { assertEquals(10, new IterationMetrics(40, 35, 5, 2).spillover()); }
    @Test void detectsScopeCreep() { assertTrue(new IterationMetrics(40, 35, 5, 2).hasScopeCreep()); assertFalse(new IterationMetrics(40, 35, 0, 2).hasScopeCreep()); }
    @Test void calculatesCompletionRate() { assertEquals(80, new IterationMetrics(40, 36, 5, 2).completionRate(), 0.001); }
    @Test void calculatesPredictability() { assertEquals(87.5, new IterationMetrics(40, 35, 5, 2).predictability(), 0.001); }
    @Test void calculatesScopeChangeRate() { assertEquals(12.5, new IterationMetrics(40, 35, 5, 2).scopeChangeRate(), 0.001); }
    @Test void calculatesDefectRate() { assertEquals(10, new IterationMetrics(40, 40, 0, 4).defectRate(), 0.001); }
    @Test void calculatesQualityScore() { assertEquals(90, new IterationMetrics(40, 40, 0, 4).qualityScore(), 0.001); }
    @Test void evaluatesSuccessAndStatus() {
        assertTrue(new IterationMetrics(40, 40, 0, 2).isSuccessful());
        assertEquals("GREEN", new IterationMetrics(40, 40, 0, 2).status());
        assertEquals("AMBER", new IterationMetrics(40, 30, 0, 2).status());
        assertEquals("RED", new IterationMetrics(40, 20, 0, 2).status());
    }
    @Test void validatesInputsAndEmptyIteration() {
        assertThrows(IllegalArgumentException.class, () -> new IterationMetrics(-1, 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new IterationMetrics(5, 6, 0, 0));
        IterationMetrics empty = new IterationMetrics(0, 0, 0, 0);
        assertEquals(0, empty.completionRate(), 0.001); assertEquals(100, empty.qualityScore(), 0.001);
    }
}

