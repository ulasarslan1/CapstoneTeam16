package OrderManagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import StorageManagement.*;
import Exceptions.StorageException;

import static org.junit.jupiter.api.Assertions.*;

public class OrderManagerTest {

    private StorageManager storageManager;
    private StorageLocation loc1;
    private OrderManager orderManager;

    @BeforeEach
    void setUp() throws Exception { // changed: throws Exception
        storageManager = new StorageManager("RA-01");
        loc1 = new StorageLocation("A1", 50);
        storageManager.addStorageLocation(loc1);
        orderManager = new OrderManager(storageManager);

        // add some initial stock
        storageManager.addStock(loc1, 20);
        waitUntil(() -> loc1.getCurrentLoad() == 20);
    }

    @Test
    void testCreateAndGetOrder() {
        Medicine med = new Medicine("Paracetamol", "B-001", 10);
        orderManager.createOrder("O-001", med, 5);

        Order order = orderManager.getOrderById("O-001");
        assertNotNull(order);
        assertEquals("PENDING", order.getStatus());
        assertEquals("Paracetamol", order.getMedicine().getName());
    }

    @Test
    void testCompleteOrderSuccessfully() throws Exception {
        Medicine med = new Medicine("Amoxicillin", "A-009", 5);
        orderManager.createOrder("O-002", med, 5);
        orderManager.completeOrder("O-002", loc1);

        waitUntil(() -> loc1.getCurrentLoad() == 15); // 20 - 5 = 15
        assertEquals(15, loc1.getCurrentLoad());

        Order order = orderManager.getOrderById("O-002");
        assertEquals("COMPLETED", order.getStatus());
    }

    @Test
    void testCancelOrder() {
        Medicine med = new Medicine("Ibuprofen", "I-777", 4);
        orderManager.createOrder("O-003", med, 4);
        orderManager.cancelOrder("O-003");

        Order order = orderManager.getOrderById("O-003");
        assertEquals("CANCELLED", order.getStatus());
    }

    @Test
    void testCompleteOrderFailsIfStockInsufficient() throws Exception {
        Medicine med = new Medicine("Morphine", "M-010", 100); // too much
        orderManager.createOrder("O-004", med, 100);
        orderManager.completeOrder("O-004", loc1);

        waitShort();
        assertEquals(20, loc1.getCurrentLoad()); // unchanged
        assertEquals("PENDING", orderManager.getOrderById("O-004").getStatus());
    }

    // helper waiting function
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
