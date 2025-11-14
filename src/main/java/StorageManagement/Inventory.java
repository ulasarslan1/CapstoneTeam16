package StorageManagement;

import java.util.HashMap;
import java.util.Map;

import Exceptions.StorageException;
import Logging.SystemLogger;

public class Inventory {

    private Map<String, Integer> stockMap;
    SystemLogger logger = new SystemLogger();

    public Inventory() {
        this.stockMap = new HashMap<>();
    }

    public void updateStock(StorageLocation location, int change) throws StorageException {
        int newStock = getStock(location) + change;
        if (newStock < 0) {
            throw new StorageException("Stock cannot be negative");
        }
        stockMap.put(location.getId(), newStock);
    }


    public int getStock(StorageLocation location) {
        return stockMap.getOrDefault(location.getId(), 0);
    }

    public int countItems(StorageLocation location) {
        return getStock(location);
    }

    public void printInventory() {
    	logger.logInfo("Inventory", "---- INVENTORY STATUS ----");

    	for (String id : stockMap.keySet()) {
    	    int qty = stockMap.get(id);
    	    logger.logInfo("Inventory", "Location " + id + ": " + qty + " items");
    	}
    }
}
