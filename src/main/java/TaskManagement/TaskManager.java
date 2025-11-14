package TaskManagement;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Logging.SystemLogger;

public class TaskManager implements TaskAssignable {

    private TaskQueue taskQueue;
    private ExecutorService executor;
    private SystemLogger logger;

    public TaskManager(int parallelTasks) {
        this.taskQueue = new TaskQueue();
        this.executor = Executors.newFixedThreadPool(parallelTasks);
        this.logger = new SystemLogger();
    }

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }
    
    public void queueTask(Task task) {
        taskQueue.addTask(task);
        logger.logInfo("TaskManager", "Task added to queue: " + task.getId());
    }

    @Override
    public void createTask(String type, String source, String destination, String status, String createdAt) {
        Task task = new Task(type, source, destination, status, createdAt);
        taskQueue.addTask(task);
        logger.logInfo("TaskManager", "Created task: " + task);
    }

    @Override
    public void assignTask(Task task) {
        task.setStatus("IN_PROGRESS");
        logger.logInfo("TaskManager", "Assigned task: " + task);
    }

    @Override
    public void completeTask(Task task) {
        task.setStatus("COMPLETED");
        logger.logInfo("TaskManager", "Completed task: " + task.getId());
    }

    @Override
    public void failTask(Task task) {
        task.setStatus("FAILED");
        logger.logError("TaskManager", "Failed task: " + task.getId());
    }

    public Task assignNextTask() {
        if (taskQueue.isEmpty()) {
            return null;
        }
        Task next = taskQueue.getNextTask();
        assignTask(next);
        return next;
    }

    public void processTasks() {
        while (!taskQueue.isEmpty()) {
            Task task = assignNextTask();

            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000); 
                        completeTask(task);
                    } catch (Exception e) {
                        failTask(task);
                    }
                }
            });
        }
    }

    public boolean hasPendingTasks() {
        return !taskQueue.isEmpty();
    }

    public void shutdown() {
        executor.shutdown();
        logger.logInfo("TaskManager", "Executor service shutdown.");
    }
}
