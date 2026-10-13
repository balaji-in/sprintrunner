package com.sprintrunner;

/** A prioritized unit of work tracked through a sprint. */
public final class BacklogItem {
    public enum Status { TODO, IN_PROGRESS, DONE }

    private final String key;
    private final String title;
    private int points;
    private final int priority;
    private Status status = Status.TODO;

    public BacklogItem(String key, String title, int points, int priority) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key is required");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        requirePositive(points, "Points");
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("Priority must be between 1 and 5");
        }
        this.key = key.trim();
        this.title = title.trim();
        this.points = points;
        this.priority = priority;
    }

    public String getKey() { return key; }
    public String getTitle() { return title; }
    public int getPoints() { return points; }
    public int getPriority() { return priority; }
    public Status getStatus() { return status; }
    public boolean isDone() { return status == Status.DONE; }

    public void start() {
        if (status == Status.DONE) {
            throw new IllegalStateException("Completed work must be reopened first");
        }
        status = Status.IN_PROGRESS;
    }

    public void complete() { status = Status.DONE; }
    public void reopen() { status = Status.TODO; }

    public void resize(int newPoints) {
        requirePositive(newPoints, "Points");
        points = newPoints;
    }

    public String priorityLabel() {
        return priority <= 2 ? "HIGH" : priority == 3 ? "MEDIUM" : "LOW";
    }

    private static void requirePositive(int value, String field) {
        if (value <= 0) {
            throw new IllegalArgumentException(field + " must be positive");
        }
    }
}

