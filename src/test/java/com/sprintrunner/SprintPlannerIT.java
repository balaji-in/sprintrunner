package com.sprintrunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

/** Functional smoke tests for the complete planning and greeting flow. */
class SprintPlannerIT {

    @Test
    void createsAReleaseReadySprintSummary() {
        SprintPlanner.SprintPlan plan = new SprintPlanner().plan(
                "Release Demo", 20, Arrays.asList(8, 5, 3, 2, 5));
        String message = new Greet().sayHello(plan.getSprintName());

        assertEquals("Hello Release Demo", message);
        assertEquals(18, plan.getCommittedPoints());
        assertEquals(4, plan.getCommittedItems());
        assertEquals("ON_TRACK", plan.getStatus());
    }
}

