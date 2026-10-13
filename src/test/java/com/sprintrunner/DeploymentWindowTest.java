package com.sprintrunner;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DeploymentWindowTest {
    @Test void calculatesDayWindowDuration() { assertEquals(4, new DeploymentWindow(9, 13, false).durationHours()); }
    @Test void calculatesOvernightDuration() { assertEquals(4, new DeploymentWindow(22, 2, false).durationHours()); }
    @Test void identifiesOvernightWindow() { assertTrue(new DeploymentWindow(22, 2, false).isOvernight()); assertFalse(new DeploymentWindow(9, 13, false).isOvernight()); }
    @Test void checksHourContainment() {
        DeploymentWindow window = new DeploymentWindow(9, 13, false);
        assertTrue(window.contains(9)); assertTrue(window.contains(12)); assertFalse(window.contains(13));
    }
    @Test void checksOvernightContainment() {
        DeploymentWindow window = new DeploymentWindow(22, 2, false);
        assertTrue(window.contains(23)); assertTrue(window.contains(1)); assertFalse(window.contains(12));
    }
    @Test void evaluatesAllowedDeployments() {
        assertTrue(new DeploymentWindow(9, 13, false).allows(3, false));
        assertFalse(new DeploymentWindow(9, 13, false).allows(5, true));
        assertFalse(new DeploymentWindow(9, 13, true).allows(2, false));
    }
    @Test void calculatesRemainingHours() {
        assertEquals(2, new DeploymentWindow(9, 13, false).remainingHoursAt(11));
        assertEquals(3, new DeploymentWindow(22, 2, false).remainingHoursAt(23));
        assertEquals(0, new DeploymentWindow(9, 13, false).remainingHoursAt(15));
    }
    @Test void exposesConfigurationAndLabel() {
        DeploymentWindow window = new DeploymentWindow(9, 13, true);
        assertEquals(9, window.getStartHour()); assertEquals(13, window.getEndHour());
        assertTrue(window.isEmergencyOnly()); assertEquals("09:00-13:00", window.label());
    }
    @Test void comparesWindowLengths() {
        assertTrue(new DeploymentWindow(9, 17, false).isLongerThan(new DeploymentWindow(9, 12, false)));
        assertThrows(IllegalArgumentException.class, () -> new DeploymentWindow(9, 17, false).isLongerThan(null));
    }
    @Test void validatesInputs() {
        assertThrows(IllegalArgumentException.class, () -> new DeploymentWindow(-1, 2, false));
        assertThrows(IllegalArgumentException.class, () -> new DeploymentWindow(2, 24, false));
        assertThrows(IllegalArgumentException.class, () -> new DeploymentWindow(2, 2, false));
        assertThrows(IllegalArgumentException.class, () -> new DeploymentWindow(9, 13, false).allows(0, false));
        assertThrows(IllegalArgumentException.class, () -> new DeploymentWindow(9, 13, false).contains(24));
    }
}
