package com.sprintrunner;

/** Tracks the usable hours available to a sprint team. */
public final class SprintCapacity {
    private final int teamMembers;
    private final int workingDays;
    private final double hoursPerDay;
    private final double focusFactor;
    private double reservedHours;

    public SprintCapacity(int teamMembers, int workingDays, double hoursPerDay, double focusFactor) {
        if (teamMembers <= 0 || workingDays <= 0 || hoursPerDay <= 0) {
            throw new IllegalArgumentException("Team size, days, and hours must be positive");
        }
        if (focusFactor <= 0 || focusFactor > 1) {
            throw new IllegalArgumentException("Focus factor must be greater than 0 and at most 1");
        }
        this.teamMembers = teamMembers;
        this.workingDays = workingDays;
        this.hoursPerDay = hoursPerDay;
        this.focusFactor = focusFactor;
    }

    public double totalHours() { return teamMembers * workingDays * hoursPerDay; }
    public double focusedHours() { return totalHours() * focusFactor; }
    public double remainingHours() { return focusedHours() - reservedHours; }
    public boolean canReserve(double hours) { return hours >= 0 && hours <= remainingHours(); }
    public boolean isFullyAllocated() { return remainingHours() == 0.0; }

    public int storyPointCapacity(double hoursPerPoint) {
        if (hoursPerPoint <= 0) {
            throw new IllegalArgumentException("Hours per point must be positive");
        }
        return (int) Math.floor(focusedHours() / hoursPerPoint);
    }

    public void reserve(double hours) {
        if (hours <= 0 || !canReserve(hours)) {
            throw new IllegalArgumentException("Reservation exceeds available capacity");
        }
        reservedHours += hours;
    }

    public void release(double hours) {
        if (hours <= 0 || hours > reservedHours) {
            throw new IllegalArgumentException("Release exceeds reserved capacity");
        }
        reservedHours -= hours;
    }

    public double utilizationPercent() {
        return reservedHours / focusedHours() * 100.0;
    }

    public void reset() { reservedHours = 0.0; }
}

