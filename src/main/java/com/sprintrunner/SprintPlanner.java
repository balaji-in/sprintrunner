package com.sprintrunner;

import java.util.List;

/** Plans work into a sprint without exceeding the team's capacity. */
public final class SprintPlanner {

    /**
     * Creates a plan by accepting backlog items in priority order.
     *
     * @param sprintName display name of the sprint
     * @param capacity maximum story points available
     * @param estimates story-point estimates in priority order
     * @return an immutable summary of the plan
     */
    public SprintPlan plan(String sprintName, int capacity, List<Integer> estimates) {
        if (sprintName == null || sprintName.trim().isEmpty()) {
            throw new IllegalArgumentException("Sprint name is required");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        if (estimates == null) {
            throw new IllegalArgumentException("Estimates are required");
        }

        int committedPoints = 0;
        int committedItems = 0;
        for (Integer estimate : estimates) {
            if (estimate == null || estimate <= 0) {
                throw new IllegalArgumentException("Every estimate must be positive");
            }
            if (committedPoints + estimate <= capacity) {
                committedPoints += estimate;
                committedItems++;
            }
        }

        String status = committedPoints == capacity ? "AT_CAPACITY" : "ON_TRACK";
        return new SprintPlan(
                sprintName.trim(), committedItems, committedPoints,
                capacity - committedPoints, status);
    }

    /** Immutable sprint planning result. */
    public static final class SprintPlan {
        private final String sprintName;
        private final int committedItems;
        private final int committedPoints;
        private final int remainingCapacity;
        private final String status;

        SprintPlan(String sprintName, int committedItems, int committedPoints,
                   int remainingCapacity, String status) {
            this.sprintName = sprintName;
            this.committedItems = committedItems;
            this.committedPoints = committedPoints;
            this.remainingCapacity = remainingCapacity;
            this.status = status;
        }

        public String getSprintName() {
            return sprintName;
        }

        public int getCommittedItems() {
            return committedItems;
        }

        public int getCommittedPoints() {
            return committedPoints;
        }

        public int getRemainingCapacity() {
            return remainingCapacity;
        }

        public String getStatus() {
            return status;
        }
    }
}

