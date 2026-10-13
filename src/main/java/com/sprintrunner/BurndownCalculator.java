package com.sprintrunner;

/** Computes common sprint burndown indicators. */
public final class BurndownCalculator {
    private final int totalPoints;
    private final int totalDays;

    public BurndownCalculator(int totalPoints, int totalDays) {
        if (totalPoints <= 0 || totalDays <= 0) {
            throw new IllegalArgumentException("Points and days must be positive");
        }
        this.totalPoints = totalPoints;
        this.totalDays = totalDays;
    }

    public int remaining(int completedPoints) {
        validateCompleted(completedPoints);
        return totalPoints - completedPoints;
    }

    public int pointsBurned(int completedPoints) {
        validateCompleted(completedPoints);
        return completedPoints;
    }

    public double percentComplete(int completedPoints) {
        return pointsBurned(completedPoints) / (double) totalPoints * 100.0;
    }

    public double idealRemaining(int day) {
        validateDay(day);
        return totalPoints * (1.0 - day / (double) totalDays);
    }

    public double dailyBurnRate(int completedPoints, int elapsedDays) {
        validateElapsed(elapsedDays);
        return pointsBurned(completedPoints) / (double) elapsedDays;
    }

    public double requiredDailyRate(int completedPoints, int elapsedDays) {
        validateElapsed(elapsedDays);
        int daysLeft = totalDays - elapsedDays;
        return daysLeft == 0 ? remaining(completedPoints) : remaining(completedPoints) / (double) daysLeft;
    }

    public boolean isAhead(int completedPoints, int day) {
        return remaining(completedPoints) <= idealRemaining(day);
    }

    public int projectedCompletionDays(int completedPoints, int elapsedDays) {
        double rate = dailyBurnRate(completedPoints, elapsedDays);
        return rate == 0 ? Integer.MAX_VALUE : (int) Math.ceil(totalPoints / rate);
    }

    public boolean isComplete(int completedPoints) { return remaining(completedPoints) == 0; }

    public int daysRemaining(int elapsedDays) {
        if (elapsedDays < 0 || elapsedDays > totalDays) {
            throw new IllegalArgumentException("Elapsed days are outside the sprint");
        }
        return totalDays - elapsedDays;
    }

    private void validateCompleted(int completedPoints) {
        if (completedPoints < 0 || completedPoints > totalPoints) {
            throw new IllegalArgumentException("Completed points are outside sprint scope");
        }
    }

    private void validateDay(int day) {
        if (day < 0 || day > totalDays) {
            throw new IllegalArgumentException("Day is outside the sprint");
        }
    }

    private void validateElapsed(int elapsedDays) {
        if (elapsedDays <= 0 || elapsedDays > totalDays) {
            throw new IllegalArgumentException("Elapsed days must be within the sprint");
        }
    }
}

