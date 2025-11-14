package TaskManagement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskQueueTest {

    @Test
    public void testAddAndRetrieveTask() {
        TaskQueue queue = new TaskQueue();
        Task task = new Task("T-1001", "DISPENSE", "ZONE A", "PATIENT 1", "PENDING");
        queue.addTask(task);

        assertFalse(queue.isEmpty());
        Task retrieved = queue.getNextTask();
        assertEquals(task.getId(), retrieved.getId());
        assertTrue(queue.isEmpty());
    }

    @Test
    public void testQueueSize() {
        TaskQueue queue = new TaskQueue();
        queue.addTask(new Task("T-2001", "RESTOCK", "ZONE B", "STORAGE", "PENDING"));
        queue.addTask(new Task("T-2002", "DISPENSE", "ZONE C", "PATIENT 2", "PENDING"));
        assertEquals(2, queue.size());
    }
}
