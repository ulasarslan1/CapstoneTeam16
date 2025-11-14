package Exceptions;

import java.util.HashMap;
import java.util.Map;

public class MedicineStorage {
    private final Map<String, Integer> stock = new HashMap<>();

    public void addMedicine(String name, int qty) {
        stock.put(name, stock.getOrDefault(name, 0) + qty);
    }

    public void removeMedicine(String name, int qty) {
        int current = stock.getOrDefault(name, 0);
        if (qty > current) {
            throw new IllegalArgumentException("Not enough medicine in stock");
        }
        stock.put(name, current - qty);
    }

    public int getMedicineCount(String name) {
        return stock.getOrDefault(name, 0);
    }
}
