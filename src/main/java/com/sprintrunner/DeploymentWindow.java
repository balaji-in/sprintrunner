package com.sprintrunner;

/** Describes a daily deployment window using whole hours. */
public final class DeploymentWindow {
    private final int startHour;
    private final int endHour;
    private final boolean emergencyOnly;

    public DeploymentWindow(int startHour, int endHour, boolean emergencyOnly) {
        validateHour(startHour);
        validateHour(endHour);
        if (startHour == endHour) {
            throw new IllegalArgumentException("Window must have a duration");
        }
        this.startHour = startHour;
        this.endHour = endHour;
        this.emergencyOnly = emergencyOnly;
    }

    public int durationHours() {
        return isOvernight() ? 24 - startHour + endHour : endHour - startHour;
    }

    public boolean isOvernight() { return endHour < startHour; }

    public boolean contains(int hour) {
        validateHour(hour);
        return isOvernight() ? hour >= startHour || hour < endHour : hour >= startHour && hour < endHour;
    }

    public boolean allows(int durationHours, boolean emergency) {
        if (durationHours <= 0) { throw new IllegalArgumentException("Duration must be positive"); }
        return durationHours <= durationHours() && (!emergencyOnly || emergency);
    }

    public int remainingHoursAt(int hour) {
        if (!contains(hour)) { return 0; }
        return isOvernight() && hour >= startHour ? 24 - hour + endHour : endHour - hour;
    }

    public boolean isEmergencyOnly() { return emergencyOnly; }
    public int getStartHour() { return startHour; }
    public int getEndHour() { return endHour; }
    public String label() { return String.format("%02d:00-%02d:00", startHour, endHour); }

    public boolean isLongerThan(DeploymentWindow other) {
        if (other == null) { throw new IllegalArgumentException("Other window is required"); }
        return durationHours() > other.durationHours();
    }

    private static void validateHour(int hour) {
        if (hour < 0 || hour > 23) { throw new IllegalArgumentException("Hour must be between 0 and 23"); }
    }
}
