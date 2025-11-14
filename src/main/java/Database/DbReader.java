package Database;

import ChargingStation.AGV;
import StorageManagement.StorageLocation;
import TaskManagement.Task;
import OrderManagement.Medicine;
import OrderManagement.Order;
import Logging.SystemLogger;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DbReader {

    private static String BASE_PATH = "src/main/resources/database/";
    private static SystemLogger logger = new SystemLogger();

    
    public static List<AGV> loadAGVs() {
        return loadCSV(BASE_PATH + "agv.csv", new CSVMapper<AGV>() {
            public AGV map(String[] parts) {
                return new AGV(parts[0], Integer.parseInt(parts[1]), Boolean.parseBoolean(parts[2]));
            }
        });
    }

    
    public static List<Task> loadTasks() {
        return loadCSV(BASE_PATH + "task.csv", new CSVMapper<Task>() {
            public Task map(String[] parts) {
                return new Task(parts[0], parts[1], parts[2], parts[3], parts[4]);
            }
        });
    }

    
    public static List<StorageLocation> loadStorageLocations() {
        return loadCSV(BASE_PATH + "storagelocation.csv", new CSVMapper<StorageLocation>() {
            public StorageLocation map(String[] parts) {
                try {
                    return new StorageLocation(parts[0], Integer.parseInt(parts[1]));
                } catch (Exception e) {
                    logger.logWarning("DatabaseReader", "Invalid storage line skipped: " + Arrays.toString(parts));
                    return null;
                }
            }
        });
    }

    
    public static List<String> loadChargingStations() {
        List<String> ids = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(BASE_PATH + "chargingstation.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("stationId")) continue;
                String[] parts = line.split(",");
                if (parts.length > 0) ids.add(parts[0].trim());
            }
            logger.logInfo("DatabaseReader", "Loaded " + ids.size() + " charging stations.");
        } catch (IOException e) {
            logger.logError("DatabaseReader", "Failed to load charging stations: " + e.getMessage());
        }
        return ids;
    }

    
    public static List<Medicine> loadMedicines() {
        return loadCSV(BASE_PATH + "medicine.csv", new CSVMapper<Medicine>() {
            public Medicine map(String[] parts) {
                return new Medicine(parts[0], parts[1], Integer.parseInt(parts[2]));
            }
        });
    }

   
    public static List<Order> loadOrders() {

        return loadCSV(BASE_PATH + "order.csv", new CSVMapper<Order>() {
            public Order map(String[] parts) {

                String orderId = parts[0];
                String medName = parts[1];
                int quantity = Integer.parseInt(parts[2]);
                String status = parts[3];
                String createdAt = parts[4];

                
                Medicine medicine = new Medicine(medName, "UNKNOWN", quantity);

                Order order = new Order(orderId, medicine, quantity, status);

                
                try {
                    var field = Order.class.getDeclaredField("createdAt");
                    field.setAccessible(true);
                    field.set(order, createdAt);
                } catch (Exception ignored) {}

                return order;
            }
        });
    }


   
    private static <T> List<T> loadCSV(String path, CSVMapper<T> mapper) {
        List<T> list = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("id") || line.startsWith("orderId") || line.startsWith("name")) 
                    continue;

                String[] parts = line.split(",");
                T obj = mapper.map(parts);
                if (obj != null) list.add(obj);
            }
            logger.logInfo("DatabaseReader", "Loaded " + list.size() + " records from " + path);
        } catch (IOException e) {
            logger.logError("DatabaseReader", "Failed to read CSV (" + path + "): " + e.getMessage());
        }
        return list;
    }

    
    private interface CSVMapper<T> {
        T map(String[] parts);
    }
}
