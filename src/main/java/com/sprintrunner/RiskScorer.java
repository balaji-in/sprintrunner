package com.sprintrunner;

/** Scores delivery risk using likelihood and impact values from one to five. */
public final class RiskScorer implements Comparable<RiskScorer> {
    private final int likelihood;
    private final int impact;

    public RiskScorer(int likelihood, int impact) {
        validateFactor(likelihood);
        validateFactor(impact);
        this.likelihood = likelihood;
        this.impact = impact;
    }

    public int getLikelihood() { return likelihood; }
    public int getImpact() { return impact; }
    public int score() { return likelihood * impact; }

    public String level() {
        int score = score();
        if (score <= 4) { return "LOW"; }
        if (score <= 9) { return "MEDIUM"; }
        if (score <= 16) { return "HIGH"; }
        return "CRITICAL";
    }

    public boolean isReleaseBlocking() { return score() >= 16; }

    public double residualScore(double mitigationPercent) {
        if (mitigationPercent < 0 || mitigationPercent > 100) {
            throw new IllegalArgumentException("Mitigation must be between 0 and 100");
        }
        return score() * (1.0 - mitigationPercent / 100.0);
    }

    public double financialExposure(double lossAmount) {
        if (lossAmount < 0) {
            throw new IllegalArgumentException("Loss amount cannot be negative");
        }
        return lossAmount * likelihood / 5.0;
    }

    public RiskScorer withLowerImpact() { return new RiskScorer(likelihood, Math.max(1, impact - 1)); }
    public RiskScorer withHigherLikelihood() { return new RiskScorer(Math.min(5, likelihood + 1), impact); }

    @Override
    public int compareTo(RiskScorer other) { return Integer.compare(score(), other.score()); }

    private static void validateFactor(int factor) {
        if (factor < 1 || factor > 5) {
            throw new IllegalArgumentException("Risk factors must be between 1 and 5");
        }
    }
}
