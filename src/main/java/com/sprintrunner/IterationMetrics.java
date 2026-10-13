package com.sprintrunner;

/** Summarizes delivery and quality outcomes for one iteration. */
public final class IterationMetrics {
    private final int committed;
    private final int completed;
    private final int added;
    private final int defects;

    public IterationMetrics(int committed, int completed, int added, int defects) {
        if (committed < 0 || completed < 0 || added < 0 || defects < 0) {
            throw new IllegalArgumentException("Metrics cannot be negative");
        }
        if (completed > committed + added) {
            throw new IllegalArgumentException("Completed work exceeds final scope");
        }
        this.committed = committed;
        this.completed = completed;
        this.added = added;
        this.defects = defects;
    }

    public int finalScope() { return committed + added; }
    public int spillover() { return finalScope() - completed; }
    public boolean hasScopeCreep() { return added > 0; }
    public double completionRate() { return percentage(completed, finalScope()); }
    public double predictability() { return percentage(Math.min(completed, committed), committed); }
    public double scopeChangeRate() { return percentage(added, committed); }
    public double defectRate() { return percentage(defects, completed); }
    public double qualityScore() { return Math.max(0.0, 100.0 - defectRate()); }
    public boolean isSuccessful() { return completionRate() >= 90.0 && defectRate() <= 10.0; }

    public String status() {
        if (isSuccessful()) { return "GREEN"; }
        if (completionRate() >= 70.0) { return "AMBER"; }
        return "RED";
    }

    private static double percentage(int value, int total) {
        return total == 0 ? 0.0 : value / (double) total * 100.0;
    }
}

