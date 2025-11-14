package OrderManagement;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Order {
    private String orderId;
    private Medicine medicine;
    private int quantity;
    private String status;
    private String createdAt;

    public Order(String orderId, Medicine medicine, int quantity, String status) {
        this.orderId = orderId;
        this.medicine = medicine;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public Order(String orderId, Medicine medicine) {
        this(orderId, medicine, medicine.getQuantity(), "CREATED");
    }

    public String getOrderId() {
        return orderId;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Order %s for %s x%d [%s]", orderId, medicine.getName(), quantity, status);
    }
}
