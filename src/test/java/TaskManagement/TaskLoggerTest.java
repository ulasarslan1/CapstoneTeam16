package TaskManagement;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TaskLoggerTest {
    private final String logFilePath;

    public TaskLoggerTest(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public void log(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Task log failed: " + e.getMessage());
        }
    }
}