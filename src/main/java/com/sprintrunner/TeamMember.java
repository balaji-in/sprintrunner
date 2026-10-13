package com.sprintrunner;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/** Tracks an engineer's skills and sprint allocation. */
public final class TeamMember {
    private final String name;
    private final double capacityHours;
    private final Set<String> skills = new LinkedHashSet<>();
    private double assignedHours;

    public TeamMember(String name, double capacityHours) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (capacityHours <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.name = name.trim();
        this.capacityHours = capacityHours;
    }

    public String getName() { return name; }
    public double availableHours() { return capacityHours - assignedHours; }
    public boolean canAccept(double hours) { return hours >= 0 && hours <= availableHours(); }
    public boolean isFullyAllocated() { return availableHours() == 0.0; }

    public void assign(double hours) {
        if (hours <= 0 || !canAccept(hours)) {
            throw new IllegalArgumentException("Assignment exceeds available hours");
        }
        assignedHours += hours;
    }

    public void release(double hours) {
        if (hours <= 0 || hours > assignedHours) {
            throw new IllegalArgumentException("Release exceeds assigned hours");
        }
        assignedHours -= hours;
    }

    public double utilizationPercent() { return assignedHours / capacityHours * 100.0; }
    public void resetAllocation() { assignedHours = 0.0; }

    public void addSkill(String skill) {
        if (skill == null || skill.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill is required");
        }
        skills.add(skill.trim().toLowerCase());
    }

    public boolean supports(String skill) {
        return skill != null && skills.contains(skill.trim().toLowerCase());
    }

    public Set<String> getSkills() { return Collections.unmodifiableSet(skills); }
}

