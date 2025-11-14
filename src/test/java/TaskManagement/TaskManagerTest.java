package TaskManagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    private TaskManager manager;

    @BeforeEach
    public void setup() {
        manager = new TaskManager(3);
    }

    @Test
    public void testCreateAndAssignTask() {
        manager.createTask("T-01", "DISPENSE", "ZONE A", "PATIENT 1", "PENDING");
        assertTrue(manager.hasPendingTasks());

        Task task = manager.assignNextTask();
        assertNotNull(task);
        assertEquals("IN_PROGRESS", task.getStatus());
    }

    @Test
    public void testCompleteTask() {
        manager.createTask("T-02", "RESTOCK", "ZONE B", "STORAGE", "PENDING");
        Task task = manager.assignNextTask();
        assertNotNull(task);

        manager.completeTask(task);
        assertEquals("COMPLETED", task.getStatus());
    }

    @Test
    public void testFailTask() {
        manager.createTask("T-03", "RETRIEVE", "ZONE C", "DISPENSARY", "PENDING");
        Task task = manager.assignNextTask();
        assertNotNull(task);

        manager.failTask(task);
        assertEquals("FAILED", task.getStatus());
    }

    @Test
    public void testProcessTasksExecutesAll() throws InterruptedException {
        manager.createTask("T-A", "MOVE", "A", "B", "PENDING");
        manager.createTask("T-B", "LOAD", "C", "D", "PENDING");
        manager.createTask("T-C", "UNLOAD", "E", "F", "PENDING");

        manager.processTasks();
        Thread.sleep(1500);

        assertFalse(manager.hasPendingTasks(), "All tasks should be moved from the queue");
    }
}
