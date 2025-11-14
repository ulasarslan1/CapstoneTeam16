package TaskManagement;

import java.util.LinkedList;
import java.util.Queue;

public class TaskQueue {
    private Queue<Task> queue = new LinkedList<>();

    public void addTask(Task task) {
        queue.add(task);
    }

    public Task getNextTask() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }
}

