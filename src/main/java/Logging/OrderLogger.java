package Logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class OrderLogger {

    private static final String LOG_FILE = "logs/order_management.log";

    public void logInfo(String message) {
        log("INFO", message);
    }

    public void logWarning(String message) {
        log("WARNING", message);
    }

    public void logError(String message) {
        log("ERROR", message);
    }

    private synchronized void log(String level, String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(LocalDateTime.now() + " [" + level + "] " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
