package com.sprintrunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class SprintPlannerTest {
    private final SprintPlanner planner = new SprintPlanner();

    @Test
    void plansHighestPriorityWorkWithinCapacity() {
        SprintPlanner.SprintPlan plan = planner.plan(
                "Sprint 24", 10, Arrays.asList(5, 3, 5, 2));

        assertEquals("Sprint 24", plan.getSprintName());
        assertEquals(3, plan.getCommittedItems());
        assertEquals(10, plan.getCommittedPoints());
        assertEquals(0, plan.getRemainingCapacity());
        assertEquals("AT_CAPACITY", plan.getStatus());
    }

    @Test
    void reportsUnusedCapacity() {
        SprintPlanner.SprintPlan plan = planner.plan(
                " Demo Sprint ", 12, Arrays.asList(3, 5));

        assertEquals("Demo Sprint", plan.getSprintName());
        assertEquals(4, plan.getRemainingCapacity());
        assertEquals("ON_TRACK", plan.getStatus());
    }

    @Test
    void acceptsAnEmptyBacklog() {
        SprintPlanner.SprintPlan plan = planner.plan("Sprint 25", 8, Collections.emptyList());
        assertEquals(0, plan.getCommittedItems());
        assertEquals(8, plan.getRemainingCapacity());
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class,
                () -> planner.plan(" ", 8, Collections.emptyList()));
        assertThrows(IllegalArgumentException.class,
                () -> planner.plan("Sprint", 0, Collections.emptyList()));
        assertThrows(IllegalArgumentException.class,
                () -> planner.plan("Sprint", 8, null));
        assertThrows(IllegalArgumentException.class,
                () -> planner.plan("Sprint", 8, Arrays.asList(3, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> planner.plan("Sprint", 8, Arrays.asList(3, null)));
    }
}

