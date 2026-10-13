package com.sprintrunner;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TeamMemberTest {
    private TeamMember member() { return new TeamMember(" Balaji ", 40); }

    @Test void trimsAndReturnsName() { assertEquals("Balaji", member().getName()); }
    @Test void startsFullyAvailable() { assertEquals(40, member().availableHours(), 0.001); }
    @Test void assignsHours() { TeamMember member = member(); member.assign(12); assertEquals(28, member.availableHours(), 0.001); }
    @Test void rejectsInvalidAssignment() {
        assertThrows(IllegalArgumentException.class, () -> member().assign(0));
        assertThrows(IllegalArgumentException.class, () -> member().assign(41));
    }
    @Test void releasesHours() { TeamMember member = member(); member.assign(20); member.release(8); assertEquals(28, member.availableHours(), 0.001); }
    @Test void reportsUtilization() { TeamMember member = member(); member.assign(10); assertEquals(25, member.utilizationPercent(), 0.001); }
    @Test void detectsFullAllocationAndReset() { TeamMember member = member(); member.assign(40); assertTrue(member.isFullyAllocated()); member.resetAllocation(); assertFalse(member.isFullyAllocated()); }
    @Test void managesSkills() { TeamMember member = member(); member.addSkill(" Java "); assertTrue(member.supports("JAVA")); assertFalse(member.supports("Python")); }
    @Test void protectsSkillCollection() { TeamMember member = member(); member.addSkill("Java"); assertThrows(UnsupportedOperationException.class, () -> member.getSkills().add("SQL")); }
    @Test void validatesConstructorSkillsAndRelease() {
        assertThrows(IllegalArgumentException.class, () -> new TeamMember(" ", 40));
        assertThrows(IllegalArgumentException.class, () -> new TeamMember("A", 0));
        assertThrows(IllegalArgumentException.class, () -> member().addSkill(" "));
        assertThrows(IllegalArgumentException.class, () -> member().release(1));
        assertTrue(member().canAccept(40)); assertFalse(member().canAccept(-1));
    }
}

