package Exceptions;

/**
 * Custom checked exception used to demonstrate exception chaining.
 */
public class LogProcessingException extends Exception {

    public LogProcessingException() {
        super();
    }

    public LogProcessingException(String message) {
        super(message);
    }

    public LogProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogProcessingException(Throwable cause) {
        super(cause);
    }
}
