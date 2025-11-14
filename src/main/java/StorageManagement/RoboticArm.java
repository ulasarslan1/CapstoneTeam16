package StorageManagement;

import Exceptions.StorageException;
import Logging.SystemLogger;

public class RoboticArm {

	//private String id;
    private boolean active;
    private SystemLogger logger;

    public RoboticArm(String id) {
   // 	this.id = id;
        this.logger = new SystemLogger();
    }

    public void moveToStorage() throws StorageException {
        if (!active) {
            logger.logError("RoboticArm", "Attempted to move, but robotic arm is inactive");
            throw new StorageException("Robotic arm is inactive");
        }
        logger.logInfo("RoboticArm", "Moving to storage...");
    }

    public void activate() throws StorageException {
        if (active) {
            logger.logWarning("RoboticArm", "Attempted to activate, but robotic arm already active");
            throw new StorageException("Robotic arm already active");
        }
        active = true;
        logger.logInfo("RoboticArm", "Activated.");
    }

    public void deactivate() throws StorageException {
        if (!active) {
            logger.logWarning("RoboticArm", "Attempted to deactivate, but robotic arm already inactive");
            throw new StorageException("Robotic arm already inactive");
        }
        active = false;
        logger.logInfo("RoboticArm", "Deactivated.");
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        logger.logInfo("RoboticArm", "Set active state to: " + active);
    }
}
