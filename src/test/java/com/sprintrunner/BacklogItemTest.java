package com.sprintrunner;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class BacklogItemTest {
    @Test void createsItemWithDefaults() {
        BacklogItem item = new BacklogItem("SR-1", "Login", 5, 2);
        assertEquals("SR-1", item.getKey()); assertEquals("Login", item.getTitle());
        assertEquals(5, item.getPoints()); assertEquals(2, item.getPriority());
        assertEquals(BacklogItem.Status.TODO, item.getStatus());
    }
    @Test void trimsIdentityFields() {
        BacklogItem item = new BacklogItem(" SR-2 ", " Search ", 3, 3);
        assertEquals("SR-2", item.getKey()); assertEquals("Search", item.getTitle());
    }
    @Test void rejectsMissingKey() {
        assertThrows(IllegalArgumentException.class, () -> new BacklogItem(" ", "Title", 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new BacklogItem(null, "Title", 1, 1));
    }
    @Test void rejectsMissingTitle() {
        assertThrows(IllegalArgumentException.class, () -> new BacklogItem("SR-1", " ", 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new BacklogItem("SR-1", null, 1, 1));
    }
    @Test void rejectsInvalidPoints() {
        assertThrows(IllegalArgumentException.class, () -> new BacklogItem("SR-1", "Title", 0, 1));
    }
    @Test void rejectsInvalidPriority() {
        assertThrows(IllegalArgumentException.class, () -> new BacklogItem("SR-1", "Title", 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new BacklogItem("SR-1", "Title", 1, 6));
    }
    @Test void movesThroughLifecycle() {
        BacklogItem item = new BacklogItem("SR-1", "Title", 1, 1);
        item.start(); assertEquals(BacklogItem.Status.IN_PROGRESS, item.getStatus());
        item.complete(); assertTrue(item.isDone()); item.reopen(); assertFalse(item.isDone());
    }
    @Test void completedItemMustBeReopenedBeforeStarting() {
        BacklogItem item = new BacklogItem("SR-1", "Title", 1, 1); item.complete();
        assertThrows(IllegalStateException.class, item::start);
    }
    @Test void resizesWork() {
        BacklogItem item = new BacklogItem("SR-1", "Title", 1, 1); item.resize(8);
        assertEquals(8, item.getPoints());
        assertThrows(IllegalArgumentException.class, () -> item.resize(-1));
    }
    @Test void labelsPriority() {
        assertEquals("HIGH", new BacklogItem("1", "A", 1, 1).priorityLabel());
        assertEquals("MEDIUM", new BacklogItem("2", "B", 1, 3).priorityLabel());
        assertEquals("LOW", new BacklogItem("3", "C", 1, 5).priorityLabel());
    }
}

