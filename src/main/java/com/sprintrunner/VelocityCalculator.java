package com.sprintrunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Calculates delivery statistics from completed sprint velocities. */
public final class VelocityCalculator {
    private final List<Integer> velocities;

    public VelocityCalculator(List<Integer> velocities) {
        if (velocities == null || velocities.isEmpty()) {
            throw new IllegalArgumentException("At least one velocity is required");
        }
        this.velocities = new ArrayList<>(velocities);
        for (Integer velocity : this.velocities) {
            if (velocity == null || velocity < 0) {
                throw new IllegalArgumentException("Velocities cannot be null or negative");
            }
        }
    }

    public int total() {
        int sum = 0;
        for (int velocity : velocities) { sum += velocity; }
        return sum;
    }

    public double average() { return total() / (double) velocities.size(); }
    public int minimum() { return Collections.min(velocities); }
    public int maximum() { return Collections.max(velocities); }
    public int latest() { return velocities.get(velocities.size() - 1); }
    public int trend() { return latest() - velocities.get(0); }

    public double median() {
        List<Integer> sorted = new ArrayList<>(velocities);
        Collections.sort(sorted);
        int middle = sorted.size() / 2;
        return sorted.size() % 2 == 1
                ? sorted.get(middle)
                : (sorted.get(middle - 1) + sorted.get(middle)) / 2.0;
    }

    public double standardDeviation() {
        double average = average();
        double squared = 0;
        for (int velocity : velocities) { squared += Math.pow(velocity - average, 2); }
        return Math.sqrt(squared / velocities.size());
    }

    public boolean isStable(double maximumDeviation) {
        if (maximumDeviation < 0) {
            throw new IllegalArgumentException("Maximum deviation cannot be negative");
        }
        return standardDeviation() <= maximumDeviation;
    }

    public int forecastNext() { return (int) Math.round((average() + latest()) / 2.0); }
}

