package OrderManagement;

import StorageManagement.*;
import Exceptions.StorageException;
import java.util.*;
import Logging.SystemLogger;

public class OrderManager {

    private List<Order> orderList;
    private StorageManager storageManager;
    private SystemLogger logger;

    public OrderManager(StorageManager storageManager) {
        this.storageManager = storageManager;
        this.orderList = new ArrayList<>();
        this.logger = new SystemLogger();
    }

    public void createOrder(String id, Medicine medicine, int quantity) {
        orderList.add(new Order(id, medicine, quantity, "CREATED"));
        logger.logInfo("OrderManager", "Created order: " + id);
    }

    public void createOrder(String id, Medicine medicine) { // overloaded
        orderList.add(new Order(id, medicine));
        logger.logInfo("OrderManager", "Created emergency order: " + id);
    }

    // --- Main Order Operations ---
    public void completeOrder(String id, StorageLocation location) {
        try {
            Order order = getOrderById(id);
            if (order == null) throw new StorageException("Order not found: " + id);

            // Remove medicine stock from storage
            storageManager.removeStockSync(location, order.getQuantity());
            order.setStatus("COMPLETED");
            logger.logInfo("OrderManager", "Order " + id + " completed successfully.");

        } catch (StorageException e) {
            logger.logError("OrderManager", "Failed to complete order: " + e.getMessage());
        }
    }

    public void cancelOrder(String id) {
        Order order = getOrderById(id);
        if (order != null) {
            order.setStatus("CANCELLED");
            logger.logWarning("OrderManager", "Order cancelled: " + id);
        }
    }

    public Order getOrderById(String id) {
        for (Order o : orderList) {
            if (o.getOrderId().equals(id)) {
                return o;
            }
        }
        return null;
    }

    public List<Order> getAllOrders() {
        return orderList;
    }
}
