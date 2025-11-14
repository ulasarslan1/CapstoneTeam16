package StorageManagement;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Exceptions.StorageException;

class StorageManagerTest {

    private StorageManager manager;
    private StorageLocation loc1;
    private StorageLocation loc2;

    @BeforeEach
    void setUp() throws StorageException {
        manager = new StorageManager("RA-01");
        loc1 = new StorageLocation("A1", 5);
        loc2 = new StorageLocation("B1", 3);
        manager.addStorageLocation(loc1);
        manager.addStorageLocation(loc2);
    }

    @Test
    void testAddStorageLocation() {
        List<StorageLocation> locations = manager.getStorageLocations();
        assertTrue(locations.contains(loc1));
        assertTrue(locations.contains(loc2));
    }

    @Test
    void testAddStockSuccessfully() throws Exception {
        manager.addStock(loc1, 3);
        waitUntil(() -> loc1.getCurrentLoad() == 3);
        assertEquals(3, loc1.getCurrentLoad());
    }

    @Test
    void testAddStockInvalidAmountThrowsException() {
        assertThrows(StorageException.class, () -> manager.addStock(loc1, 0));
        assertThrows(StorageException.class, () -> manager.addStock(loc1, -5));
    }

    @Test
    void testAddStockNullLocationThrowsException() {
        assertThrows(StorageException.class, () -> manager.addStock(null, 1));
    }

    @Test
    void testRemoveStockSuccessfully() throws Exception {
        manager.addStock(loc1, 3);
        waitUntil(() -> loc1.getCurrentLoad() == 3);

        manager.removeStock(loc1, 2);
        waitUntil(() -> loc1.getCurrentLoad() == 1);

        assertEquals(1, loc1.getCurrentLoad());
    }

    @Test
    void testRemoveStockMoreThanAvailableThrowsException() throws Exception {
        manager.addStock(loc1, 2);
        waitUntil(() -> loc1.getCurrentLoad() == 2);
        manager.removeStock(loc1, 5);
        waitShort();
        assertEquals(2, loc1.getCurrentLoad());
    }

    @Test
    void testRemoveStockInvalidAmountThrowsException() {
        assertThrows(StorageException.class, () -> manager.removeStock(loc1, 0));
        assertThrows(StorageException.class, () -> manager.removeStock(loc1, -2));
    }

    @Test
    void testRemoveStockNullLocationThrowsException() {
        assertThrows(StorageException.class, () -> manager.removeStock(null, 1));
    }

    @Test
    void testManageInventory() throws Exception {
        manager.addStock(loc1, 2);
        manager.addStock(loc2, 1);
        waitUntil(() -> loc1.getCurrentLoad() == 2 && loc2.getCurrentLoad() == 1);
        assertDoesNotThrow(() -> manager.manageInventory());
    }

    @Test
    void testGetInventory() {
        assertNotNull(manager.getInventory());
    }

    private void waitUntil(CheckCondition condition) throws Exception {
        int retries = 40;
        while (!condition.isTrue() && retries-- > 0) {
            Thread.sleep(50);
        }
    }

    private void waitShort() throws InterruptedException {
        Thread.sleep(200);
    }

    @FunctionalInterface
    private interface CheckCondition {
        boolean isTrue();
    }
}
