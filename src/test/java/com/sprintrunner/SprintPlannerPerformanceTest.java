package com.sprintrunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

/** Small deterministic load test that is safe to run on a shared CI runner. */
class SprintPlannerPerformanceTest {

    @Test
    void plansOneHundredThousandSprints() {
        SprintPlanner planner = new SprintPlanner();
        long started = System.nanoTime();
        int checksum = 0;

        for (int index = 0; index < 100_000; index++) {
            checksum += planner.plan("Load Test", 20, Arrays.asList(8, 5, 3, 2, 5))
                    .getCommittedPoints();
        }

        long elapsedNanos = System.nanoTime() - started;
        double elapsedSeconds = elapsedNanos / 1_000_000_000.0;
        double throughput = 100_000 / Math.max(elapsedSeconds, 0.000001);
        System.out.printf(
                "PERFORMANCE_METRICS iterations=100000 durationSeconds=%.6f throughputPerSecond=%.2f%n",
                elapsedSeconds, throughput);

        assertEquals(1_800_000, checksum);
    }
}
