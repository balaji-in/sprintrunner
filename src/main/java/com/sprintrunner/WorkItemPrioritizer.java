package com.sprintrunner;

/** Calculates weighted-shortest-job-first style prioritization scores. */
public final class WorkItemPrioritizer {

    public double score(int businessValue, int timeCriticality, int riskReduction, int effort) {
        validateNonNegative(businessValue, "Business value");
        validateNonNegative(timeCriticality, "Time criticality");
        validateNonNegative(riskReduction, "Risk reduction");
        if (effort <= 0) { throw new IllegalArgumentException("Effort must be positive"); }
        return costOfDelay(businessValue, timeCriticality, riskReduction) / effort;
    }

    public int costOfDelay(int businessValue, int timeCriticality, int riskReduction) {
        validateNonNegative(businessValue, "Business value");
        validateNonNegative(timeCriticality, "Time criticality");
        validateNonNegative(riskReduction, "Risk reduction");
        return businessValue + timeCriticality + riskReduction;
    }

    public String band(double score) {
        if (score < 0) { throw new IllegalArgumentException("Score cannot be negative"); }
        if (score >= 10) { return "EXPEDITE"; }
        if (score >= 5) { return "HIGH"; }
        if (score >= 2) { return "MEDIUM"; }
        return "LOW";
    }

    public boolean isUrgent(int timeCriticality) {
        validateNonNegative(timeCriticality, "Time criticality");
        return timeCriticality >= 8;
    }

    public double normalizedScore(double score, double maximum) {
        if (score < 0 || maximum <= 0 || score > maximum) {
            throw new IllegalArgumentException("Invalid score range");
        }
        return score / maximum * 100.0;
    }

    public int compare(double first, double second) { return Double.compare(first, second); }
    public double higher(double first, double second) { return Math.max(first, second); }
    public double effortEfficiency(int value, int effort) { return score(value, 0, 0, effort); }
    public boolean shouldExpedite(double score) { return "EXPEDITE".equals(band(score)); }

    public double applyDependencyPenalty(double score, int blockedDependencies) {
        if (score < 0 || blockedDependencies < 0) {
            throw new IllegalArgumentException("Score and dependencies cannot be negative");
        }
        return score / (blockedDependencies + 1.0);
    }

    private static void validateNonNegative(int value, String field) {
        if (value < 0) { throw new IllegalArgumentException(field + " cannot be negative"); }
    }
}

