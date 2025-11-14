package StorageManagement;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Exceptions.StorageException;

class RoboticArmTest {

    private RoboticArm arm;

    @BeforeEach
    void setUp() {
        arm = new RoboticArm("RA-01");
    }

    @Test
    void testMoveToStorageWhenActive() throws StorageException {
        arm.setActive(true);
        assertDoesNotThrow(() -> arm.moveToStorage());
    }

    @Test
    void testMoveToStorageThrowsExceptionIfInactive() {
        arm.setActive(false);
        assertThrows(StorageException.class, () -> arm.moveToStorage());
    }

    @Test
    void testActivateSetsActiveTrue() throws StorageException {
        arm.setActive(false);
        arm.activate();
        assertTrue(arm.isActive());
    }

    @Test
    void testDeactivateSetsActiveFalse() throws StorageException {
        arm.setActive(true);
        arm.deactivate();
        assertFalse(arm.isActive());
    }

    @Test
    void testManualSetActive() {
        arm.setActive(false);
        assertFalse(arm.isActive());
        arm.setActive(true);
        assertTrue(arm.isActive());
    }

    @Test
    void testMoveToStorageAfterActivation() throws StorageException {
        arm.setActive(false);
        arm.activate();
        assertDoesNotThrow(() -> arm.moveToStorage());
    }

    @Test
    void testMoveToStorageAfterDeactivationThrowsException() throws StorageException {
        arm.setActive(true);
        arm.deactivate();
        assertThrows(StorageException.class, () -> arm.moveToStorage());
    }
}
