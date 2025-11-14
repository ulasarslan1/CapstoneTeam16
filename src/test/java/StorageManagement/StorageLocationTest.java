package StorageManagement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Exceptions.StorageException;

class StorageLocationTest {

    private StorageLocation location;

    @BeforeEach
    void setUp() throws StorageException {
        location = new StorageLocation("A1", 3);
    }

    @Test
    void testValidConstructor() throws StorageException {
        StorageLocation loc = new StorageLocation("B1", 5);
        assertEquals("B1", loc.getId());
        assertEquals(5, loc.getCapacity());
        assertEquals(0, loc.getCurrentLoad());
    }

    @Test
    void testConstructorInvalidIdThrowsException() {
        assertThrows(StorageException.class, () -> new StorageLocation(null, 5));
        assertThrows(StorageException.class, () -> new StorageLocation("", 5));
    }

    @Test
    void testConstructorInvalidCapacityThrowsException() {
        assertThrows(StorageException.class, () -> new StorageLocation("C1", 0));
        assertThrows(StorageException.class, () -> new StorageLocation("C2", -5));
    }

    @Test
    void testAddItemIncrementsLoad() throws StorageException {
        location.addItem();
        assertEquals(1, location.getCurrentLoad());
    }

    @Test
    void testAddItemThrowsExceptionWhenFull() throws StorageException {
        location.addItem();
        location.addItem();
        location.addItem();
        assertThrows(StorageException.class, () -> location.addItem());
    }

    @Test
    void testRemoveItemDecrementsLoad() throws StorageException {
        location.addItem();
        location.addItem();
        location.removeItem();
        assertEquals(1, location.getCurrentLoad());
    }

    @Test
    void testRemoveItemThrowsExceptionWhenEmpty() {
        assertThrows(StorageException.class, () -> location.removeItem());
    }

    @Test
    void testIsFull() throws StorageException {
        assertFalse(location.isFull());
        location.addItem();
        location.addItem();
        location.addItem();
        assertTrue(location.isFull());
    }
}
