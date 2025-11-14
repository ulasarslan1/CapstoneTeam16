package Exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MedicineStorageTest {

    @Test
    void testAddMedicine() {
        MedicineStorage storage = new MedicineStorage();
        storage.addMedicine("Paracetamol", 10);
        assertEquals(10, storage.getMedicineCount("Paracetamol"));
    }

    @Test
    void testRemoveMedicine() {
        MedicineStorage storage = new MedicineStorage();
        storage.addMedicine("Ibuprofen", 5);
        storage.removeMedicine("Ibuprofen", 3);
        assertEquals(2, storage.getMedicineCount("Ibuprofen"));
    }

    @Test
    void testRemoveTooMuch() {
        MedicineStorage storage = new MedicineStorage();
        storage.addMedicine("Aspirin", 2);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            storage.removeMedicine("Aspirin", 5);
        });
        assertEquals("Not enough medicine in stock", ex.getMessage());
    }
}
