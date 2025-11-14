package Exceptions;

import java.io.IOException;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;


/**
 * Demonstrates exception chaining:
 *  - IO errors are wrapped into LogProcessingException
 *  - Parsing errors are wrapped similarly
 *  - Multi-level wrapping shows nested causes
 */
public class ExceptionChainDemo {

    /**
     * Simulates processing a log file. Underlying readFile throws IOException,
     * which we catch and chain into a LogProcessingException.
     */
    public void processLogFile(String path) throws LogProcessingException {
        try {
            readFile(path);
        } catch (IOException ioe) {
            // Chain the original IOException as the cause
            throw new LogProcessingException("Failed to process log file: " + path, ioe);
        }
    }

    /**
     * Simulated low-level file read that always fails (for demo/testing).
     */
    private void readFile(String path) throws IOException {
        // Simulate an IO error
        throw new IOException("Simulated IO error for path: " + path);
    }

    /**
     * Parse a CSV-like log line and chain any parsing errors.
     */
    public void parseAndProcess(String line) throws LogProcessingException {
        try {
            parse(line);
            // Continue processing...
        } catch (IllegalArgumentException iae) {
            // Wrap parsing exceptions
            throw new LogProcessingException("Failed to parse log line", iae);
        }
    }

    private void parse(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line is null");
        }
        // Very simple CSV validation for demonstration
        if (!line.contains(",")) {
            throw new IllegalArgumentException("Invalid CSV line: missing comma");
        }
        // If valid, do nothing (demo)
    }

    /**
     * Demonstrates multi-level chaining:
     * processLogFile throws LogProcessingException (wrapping an IOException),
     * then we catch it and throw a higher-level LogProcessingException that chains the previous one.
     */
    public void multiLevelProcess(String path) throws LogProcessingException {
        try {
            processLogFile(path);
        } catch (LogProcessingException e) {
            // This creates a second-level chain: new exception -> cause is the first LogProcessingException
            throw new LogProcessingException("Higher-level processing failed for: " + path, e);
        }
    }

    /**
     * For another demonstration: wrap a runtime exception into LogProcessingException
     */
    public void wrapRuntimeError() throws LogProcessingException {
        try {
            causeRuntimeError();
        } catch (RuntimeException re) {
            throw new LogProcessingException("Wrapped runtime failure", re);
        }
    }

    private void causeRuntimeError() {
        throw new RuntimeException("Simulated runtime failure");
    }
}
