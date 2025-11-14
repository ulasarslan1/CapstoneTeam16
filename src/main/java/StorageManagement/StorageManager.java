package StorageManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import Exceptions.StorageException;
import Logging.SystemLogger;

public class StorageManager implements StockHandler {

    private List<StorageLocation> storageLocations;
    private RoboticArm roboticArm;
    private Inventory inventory;
    private SystemLogger logger;
    private ExecutorService executor;
    private ReentrantLock lock;

    public StorageManager(String roboticArmId) {
        this.storageLocations = new ArrayList<>();
        this.roboticArm = new RoboticArm(roboticArmId);
        this.inventory = new Inventory();
        this.logger = new SystemLogger();
        this.executor = Executors.newFixedThreadPool(4);
        this.lock = new ReentrantLock();
    }

    public void addStorageLocation(StorageLocation location) {
        lock.lock();
        try {
            storageLocations.add(location);
            logger.logInfo("StorageManagement", "New storage location added: " + location.getId());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void addStock(StorageLocation location, int amount) throws StorageException {
        if (location == null) {
            throw new StorageException("Failed to add stock: location is null");
        }
        if (amount <= 0) {
            throw new StorageException("Failed to add stock: invalid amount");
        }

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    lock.lock();
                    try {
                        for (int i = 0; i < amount; i++) {
                            location.addItem();
                        }
                        inventory.updateStock(location, amount);
                        logger.logInfo("StorageManagement", amount + " items added to location " + location.getId());
                    } finally {
                        lock.unlock();
                    }
                } catch (Exception e) {
                    logger.logError("StorageManagement", "Add stock failed: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void removeStock(StorageLocation location, int amount) throws StorageException {
        if (location == null) {
            throw new StorageException("Failed to remove stock: location is null");
        }
        if (amount <= 0) {
            throw new StorageException("Failed to remove stock: invalid amount");
        }

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    lock.lock();
                    try {
                        if (location.getCurrentLoad() < amount) {
                            throw new StorageException("Not enough items in storage");
                        }

                        for (int i = 0; i < amount; i++) {
                            location.removeItem();
                        }
                        inventory.updateStock(location, -amount);
                    } finally {
                        lock.unlock();
                    }

                    roboticArm.setActive(true);
                    roboticArm.moveToStorage();
                    roboticArm.deactivate();

                    logger.logInfo("StorageManagement", amount + " items removed from location " + location.getId());
                } catch (StorageException e) {
                    logger.logError("StorageManagement", "Remove stock failed: " + e.getMessage());
                }
            }
        });
    }

    public void removeStockSync(StorageLocation location, int amount) throws StorageException {
        if (location.getCurrentLoad() < amount) {
            throw new StorageException("Not enough items in storage");
        }

        // Actually remove stock immediately
        for (int i = 0; i < amount; i++) {
            location.removeItem();
        }
        inventory.updateStock(location, -amount);

        roboticArm.setActive(true);
        roboticArm.moveToStorage();
        roboticArm.deactivate();
    }


    public void manageInventory() {
    	executor.submit(new Runnable() {
    	    @Override
    	    public void run() {
    	        logger.logInfo("StorageManagement", "Inventory management started...");
    	        lock.lock();
    	        try {
    	            for (StorageLocation location : storageLocations) {
    	                int count = inventory.countItems(location);
    	                System.out.println("Inventory at " + location.getId() + ": " + count + " items.");
    	            }
    	        } finally {
    	            lock.unlock();
    	        }
    	        logger.logInfo("StorageManagement", "Inventory management completed.");
    	    }
    	});
    }

    public List<StorageLocation> getStorageLocations() {
        return storageLocations;
    }

    public Inventory getInventory() {
        return inventory;
    }

    // Graceful shutdown for thread pool
    public void shutdown() {
        logger.logInfo("StorageManagement", "Shutting down storage manager...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.logInfo("StorageManagement", "Storage manager stopped.");
    }
}
