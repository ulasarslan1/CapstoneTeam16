package Exceptions;

import Logging.SystemLogger;
import java.io.IOException;
import java.nio.file.*;

public class ExceptionHandler {
    private SystemLogger logger;

    public ExceptionHandler() {
        this.logger = new SystemLogger();
    }

    
    public boolean handleFileOperations(String fileName, String operation) {
        try {
            simulateFileOperation(fileName, operation);
            return true;
            
        } catch (FileAlreadyExistsException e) {
            logger.logError("ExceptionHandler", "File already exists: " + fileName);
            return false;
            
        } catch (NoSuchFileException e) {
            logger.logError("ExceptionHandler", "File not found: " + fileName);
            return false;
            
        } catch (AccessDeniedException e) {
            logger.logError("ExceptionHandler", "Access denied for file: " + fileName);
            return false;
            
        } catch (IOException e) {
            logger.logError("ExceptionHandler", "IO error: " + e.getMessage());
            return false;
            
        } catch (SecurityException e) {
            logger.logError("ExceptionHandler", "Security violation: " + e.getMessage());
            return false;
        }
    }

    
    public boolean handleTaskOperations(String taskId, String operation) {
        try {
            simulateTaskOperation(taskId, operation);
            return true;
            
        } catch (IllegalArgumentException e) {
            logger.logError("ExceptionHandler", "Invalid argument: " + e.getMessage());
            return false;
            
        } catch (IllegalStateException e) {
            logger.logError("ExceptionHandler", "Illegal state: " + e.getMessage());
            return false;
            
        } catch (NullPointerException e) {
            logger.logError("ExceptionHandler", "Null reference in operation");
            return false;
            
        } catch (UnsupportedOperationException e) {
            logger.logError("ExceptionHandler", "Unsupported operation: " + e.getMessage());
            return false;
        }
    }

    
    public boolean handleWithMultiCatch(String resourceName, String operation) throws NoSuchFileException {
        try {
            simulateResourceOperation(resourceName, operation);
            return true;
            
        } catch (FileAlreadyExistsException e) {
            logger.logError("ExceptionHandler", "File error: " + e.getMessage());
            return false;
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.logError("ExceptionHandler", "Validation error: " + e.getMessage());
            return false;
            
        } catch (Exception e) {
            logger.logError("ExceptionHandler", "Unexpected error: " + e.getMessage());
            return false;
        }
    }

    
    private void simulateFileOperation(String fileName, String operation) throws IOException {
        if ("create".equals(operation)) {
            throw new FileAlreadyExistsException(fileName);
        } else if ("read".equals(operation)) {
            throw new NoSuchFileException(fileName);
        }
        
    }

    private void simulateTaskOperation(String taskId, String operation) {
        if ("invalid".equals(operation)) {
            throw new IllegalArgumentException("Invalid operation");
        } else if ("null".equals(operation)) {
            throw new NullPointerException();
        }
        
    }

    private void simulateResourceOperation(String resourceName, String operation) throws FileAlreadyExistsException {
        if ("file".equals(operation)) {
            throw new FileAlreadyExistsException(resourceName);
        } else if ("validation".equals(operation)) {
            throw new IllegalArgumentException("Validation failed");
        }
        
    }
}