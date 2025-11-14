package StorageManagement;

import Exceptions.StorageException;

public interface StockHandler {
    void addStock(StorageLocation location, int amount) throws StorageException;
    void removeStock(StorageLocation location, int amount) throws StorageException;
}
