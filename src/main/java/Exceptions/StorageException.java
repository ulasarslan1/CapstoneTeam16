package Exceptions;

public class StorageException extends Exception {
	
    // Constructor with only a message
    public StorageException(String message) {
        super(message);
    }

    // Constructor with a message and a cause (chained exception)
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
