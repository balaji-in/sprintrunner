package com.sprintrunner;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class RiskScorerTest {
    @Test void exposesFactors() { RiskScorer risk = new RiskScorer(3, 4); assertEquals(3, risk.getLikelihood()); assertEquals(4, risk.getImpact()); }
    @Test void calculatesScore() { assertEquals(12, new RiskScorer(3, 4).score()); }
    @Test void assignsRiskLevels() {
        assertEquals("LOW", new RiskScorer(1, 4).level()); assertEquals("MEDIUM", new RiskScorer(3, 3).level());
        assertEquals("HIGH", new RiskScorer(4, 4).level()); assertEquals("CRITICAL", new RiskScorer(5, 5).level());
    }
    @Test void identifiesReleaseBlockingRisk() { assertTrue(new RiskScorer(4, 4).isReleaseBlocking()); assertFalse(new RiskScorer(3, 4).isReleaseBlocking()); }
    @Test void calculatesResidualScore() { assertEquals(6, new RiskScorer(3, 4).residualScore(50), 0.001); }
    @Test void calculatesFinancialExposure() { assertEquals(6000, new RiskScorer(3, 4).financialExposure(10000), 0.001); }
    @Test void lowersImpact() { assertEquals(3, new RiskScorer(3, 4).withLowerImpact().getImpact()); assertEquals(1, new RiskScorer(3, 1).withLowerImpact().getImpact()); }
    @Test void raisesLikelihood() { assertEquals(4, new RiskScorer(3, 4).withHigherLikelihood().getLikelihood()); assertEquals(5, new RiskScorer(5, 4).withHigherLikelihood().getLikelihood()); }
    @Test void comparesRisks() { assertTrue(new RiskScorer(4, 4).compareTo(new RiskScorer(2, 3)) > 0); }
    @Test void validatesInputs() {
        assertThrows(IllegalArgumentException.class, () -> new RiskScorer(0, 1));
        assertThrows(IllegalArgumentException.class, () -> new RiskScorer(1, 6));
        assertThrows(IllegalArgumentException.class, () -> new RiskScorer(2, 2).residualScore(101));
        assertThrows(IllegalArgumentException.class, () -> new RiskScorer(2, 2).financialExposure(-1));
    }
}
