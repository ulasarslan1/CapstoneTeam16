package Exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

//import Exceptions.ExceptionHandler;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionHandlerTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Should handle file already exists exception")
    void testHandleFileAlreadyExists() {
        ExceptionHandler handler = new ExceptionHandler();
        boolean result = handler.handleFileOperations("test.log", "create");
        
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle file not found exception")
    void testHandleFileNotFound() {
        ExceptionHandler handler = new ExceptionHandler();
        boolean result = handler.handleFileOperations("test.log", "read");
        
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle illegal argument exception")
    void testHandleIllegalArgument() {
        ExceptionHandler handler = new ExceptionHandler();
        boolean result = handler.handleTaskOperations("task123", "invalid");
        
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle null pointer exception")
    void testHandleNullPointer() {
        ExceptionHandler handler = new ExceptionHandler();
        boolean result = handler.handleTaskOperations("task123", "null");
        
        assertFalse(result); 
    }

    @Test
    @DisplayName("Should handle multiple exceptions with multi-catch")
    void testHandleMultiCatch() throws NoSuchFileException {
        ExceptionHandler handler = new ExceptionHandler();
        boolean result1 = handler.handleWithMultiCatch("test", "file");
        boolean result2 = handler.handleWithMultiCatch("test", "validation");
        
        assertFalse(result1);
        assertFalse(result2);
    }
}