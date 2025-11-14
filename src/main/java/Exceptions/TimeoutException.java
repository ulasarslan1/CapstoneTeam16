package Exceptions;
public class TimeoutException extends ProjectException {
    public TimeoutException(String message) { super(message); }
    public TimeoutException(String message, Throwable cause) { super(message, cause); }
}
