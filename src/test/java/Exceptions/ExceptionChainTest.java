package Exceptions;

//import org.junit.jupiter.api.Test;

//import static org.junit.jupiter.api.Assertions.*;

//package de.fhdo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;

class ExceptionChainTest {

    private final ExceptionChainDemo demo = new ExceptionChainDemo();

    @Test
    void testProcessLogFileChainsIOException() {
        LogProcessingException ex = assertThrows(LogProcessingException.class,
                () -> demo.processLogFile("logs/pharmacy/test.csv"));

        // Cause should be an IOException with the simulated message
        assertNotNull(ex.getCause(), "Cause should not be null");
        assertTrue(ex.getCause() instanceof java.io.IOException,
                "Cause should be IOException");
        assertTrue(ex.getCause().getMessage().contains("Simulated IO error"),
                "Cause message should contain simulated IO message");
        assertTrue(ex.getMessage().contains("Failed to process log file"),
                "Wrapper message should be present");
    }

    @Test
    void testParseAndProcessChainsIllegalArgument() {
        LogProcessingException ex = assertThrows(LogProcessingException.class,
                () -> demo.parseAndProcess(null));

        // Cause should be IllegalArgumentException created by parse()
        assertNotNull(ex.getCause());
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
        assertEquals("Line is null", ex.getCause().getMessage());
        assertTrue(ex.getMessage().contains("Failed to parse log line"));
    }

    @Test
    void testMultiLevelChainingHasNestedCauses() {
        LogProcessingException top = assertThrows(LogProcessingException.class,
                () -> demo.multiLevelProcess("path/level.csv"));

        // Top-level should have a cause (the lower-level LogProcessingException)
        Throwable firstCause = top.getCause();
        assertNotNull(firstCause);
        assertTrue(firstCause instanceof LogProcessingException,
                "First cause should be a LogProcessingException");

        // The inner cause should be the original IOException
        Throwable innerCause = firstCause.getCause();
        assertNotNull(innerCause);
        assertTrue(innerCause instanceof java.io.IOException,
                "Inner cause should be IOException");
        assertTrue(innerCause.getMessage().contains("Simulated IO error"));
    }

    @Test
    void testNullCauseAllowed() {
        // Construct an exception with null cause to ensure we allow null chaining
        LogProcessingException ex = new LogProcessingException("Test without cause", null);
        assertNull(ex.getCause(), "Cause should be null");
        assertEquals("Test without cause", ex.getMessage());
    }

    @Test
    void testWrapRuntimePreservesCauseAndMessage() {
        LogProcessingException ex = assertThrows(LogProcessingException.class,
                () -> demo.wrapRuntimeError());

        assertNotNull(ex.getCause());
        assertTrue(ex.getCause() instanceof RuntimeException);
        assertEquals("Simulated runtime failure", ex.getCause().getMessage());
        assertEquals("Wrapped runtime failure", ex.getMessage());
    }
}
