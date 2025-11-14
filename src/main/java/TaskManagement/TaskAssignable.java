package TaskManagement;

public interface TaskAssignable {
	void createTask(String type, String source, String destination, String status, String createdAt);
    void assignTask(Task task);
    void completeTask(Task task);
    void failTask(Task task);
    Task assignNextTask();
    boolean hasPendingTasks();	 
}