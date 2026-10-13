package com.sprintrunner;

import java.util.ArrayList;
import java.util.List;

/** Forecasts delivery across fixed-length sprints. */
public final class ReleasePlanner {
    private final int velocity;
    private final int sprintLengthDays;

    public ReleasePlanner(int velocity, int sprintLengthDays) {
        if (velocity <= 0 || sprintLengthDays <= 0) {
            throw new IllegalArgumentException("Velocity and sprint length must be positive");
        }
        this.velocity = velocity;
        this.sprintLengthDays = sprintLengthDays;
    }

    public int sprintsRequired(int points) {
        validatePoints(points);
        return points == 0 ? 0 : (int) Math.ceil(points / (double) velocity);
    }

    public int forecastDays(int points) { return sprintsRequired(points) * sprintLengthDays; }
    public int totalCapacity(int sprints) { validateSprints(sprints); return velocity * sprints; }
    public boolean canDeliver(int points, int sprints) { return points <= totalCapacity(sprints); }
    public int remainingPoints(int points, int completedSprints) {
        validatePoints(points);
        return Math.max(0, points - totalCapacity(completedSprints));
    }

    public double utilizationPercent(int points, int sprints) {
        validatePoints(points);
        int capacity = totalCapacity(sprints);
        return capacity == 0 ? 0.0 : points / (double) capacity * 100.0;
    }

    public int bufferPoints(int points, int sprints) {
        validatePoints(points);
        return Math.max(0, totalCapacity(sprints) - points);
    }

    public boolean isAtRisk(int points, int sprints) { return !canDeliver(points, sprints); }

    public List<Integer> distribute(int points) {
        validatePoints(points);
        List<Integer> plan = new ArrayList<>();
        int remaining = points;
        while (remaining > 0) {
            int allocation = Math.min(velocity, remaining);
            plan.add(allocation);
            remaining -= allocation;
        }
        return plan;
    }

    public int getVelocity() { return velocity; }
    public int getSprintLengthDays() { return sprintLengthDays; }

    private static void validatePoints(int points) {
        if (points < 0) { throw new IllegalArgumentException("Points cannot be negative"); }
    }

    private static void validateSprints(int sprints) {
        if (sprints < 0) { throw new IllegalArgumentException("Sprints cannot be negative"); }
    }
}

