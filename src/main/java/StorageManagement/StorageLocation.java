package StorageManagement;

import Exceptions.StorageException;

public class StorageLocation {

    private String id;
    private int capacity;
    private int currentLoad;

    public StorageLocation(String id, int capacity) throws StorageException {
        if (id == null || id.trim().isEmpty()) {
            throw new StorageException("Failed to create StorageLocation: invalid ID");
        }
        if (capacity <= 0) {
            throw new StorageException("Failed to create StorageLocation: invalid capacity");
        }

        this.id = id;
        this.capacity = capacity;
        this.currentLoad = 0;
    }

    public String getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    public boolean isFull() {
        return currentLoad >= capacity;
    }

    public void addItem() throws StorageException {
        if (isFull()) {
            throw new StorageException("StorageLocation is full");
        }
        currentLoad++;
    }

    public void removeItem() throws StorageException {
        if (currentLoad <= 0) {
            throw new StorageException("StorageLocation is empty");
        }
        currentLoad--;
    }
}
