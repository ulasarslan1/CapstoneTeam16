package Logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;

public class SystemLogger implements Logger {
	
    private static final Path LOG_DIR = Paths.get("logs");
    
    public SystemLogger() {
        try {
            Files.createDirectories(LOG_DIR);
        } catch (IOException e) {
            System.err.println("Failed to initialize log directory: " + e.getMessage());
        }
    }

    private void writeEntry(LogEntry entry, LocalDate date) {
        Path file = LOG_DIR.resolve(date + ".txt");
        String line = entry.toString();
        try (BufferedWriter writer = Files.newBufferedWriter(
                file, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing log entry: " + e.getMessage());
        }
    }
    
    
    private void log(String level, String component, String message) {
        writeEntry(new LogEntry(level, component, message), LocalDate.now());
    }

    
    @Override
    public void logInfo(String component, String message) {
        log("INFO", component, message);
    }

    @Override
    public void logWarning(String component, String message) {
        log("WARN", component, message);
    }

    @Override
    public void logError(String component, String message) {
        log("ERROR", component, message);
    }
    
    
    @Override
    public boolean moveLogFile(LocalDate date, String targetDirectory) {
        try {
            Path source = LOG_DIR.resolve(date + ".txt");
            Path target = Paths.get(targetDirectory).resolve(source.getFileName());
            Files.createDirectories(target.getParent());
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    
    @Override
    public boolean deleteLogFile(LocalDate date) {
        try {
            return Files.deleteIfExists(LOG_DIR.resolve(date + ".txt"));
        } catch (IOException e) {
            return false;
        }
    }
    

    @Override
    public boolean archiveLogFile(LocalDate date) {
        return moveLogFile(date, "logs/archive");
    }
    
    
    
}