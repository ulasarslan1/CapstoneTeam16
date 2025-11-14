package OrderManagement;

public class Medicine {
    private String name;
    private String batchNumber;
    private int quantity;

    public Medicine(String name, String batchNumber, int quantity) {
        this.name = name;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("Medicine{name='%s', batch='%s', qty=%d}", name, batchNumber, quantity);
    }
}
