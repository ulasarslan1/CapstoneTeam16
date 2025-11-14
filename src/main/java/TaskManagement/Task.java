package TaskManagement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Task {
    private String id;
    private String type;
    private String source;
    private String destination;
    private String status;
    private String createdAt;
    
    Random random = new Random();

    public Task(String id, String type, String source, String destination, String status) {
        this.id = id;
        this.type = type;
        this.source = source;
        this.destination = destination;
        this.status = status;
        this.createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Task ID: %s ,%s, From: %s To: %s, Status: %s", 
           id, type, source, destination, status);
    }
}
