package Exceptions;

public class ChargingException extends Exception {
	private ChargingErrorType errorType;
	// Enum for scenarios
    public enum ChargingErrorType {
        MALFUNCTION,
        LOW_BATTERY,
        CONNECTION_LOST,
        OVERHEATING,
        UNKNOWN
    }
    public ChargingException(String message) {
        super(message);
    	this.errorType = ChargingErrorType.UNKNOWN;
    }
 // Constructor: message + scenario
    public ChargingException(String message, ChargingErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public ChargingErrorType getErrorType() { return errorType; }

    @Override
    public String toString() {
        return "ChargingException{" + "type=" + errorType + ", message=" + getMessage() + '}';
    }
}
