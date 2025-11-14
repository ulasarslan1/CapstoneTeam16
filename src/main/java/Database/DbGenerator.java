package Database;

import Logging.SystemLogger;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class DbGenerator {

    private static String OUTPUT_DIR = "src/main/resources/database/";
    private static SystemLogger logger = new SystemLogger();

    public static void generateAll() throws IOException {
        Files.createDirectories(Paths.get(OUTPUT_DIR));

        
        writeFile("agv.csv", List.of(
            "id,batteryLevel,urgentOrder",
            "AGV-A1,25,true",
            "AGV-A2,60,false",
            "AGV-B1,80,false",
            "AGV-B2,55,true",
            "AGV-C1,45,true",
            "AGV-C2,95,false",
            "AGV-D1,100,false",
            "AGV-D2,30,true"
        ));

        
        writeFile("chargingstation.csv", List.of(
            "stationId,agvId",
            "ST-01,AGV-A1",
            "ST-02,AGV-B1",
            "ST-03,AGV-C1",
            "ST-04,AGV-D2",
            "ST-05,AGV-B2"
        ));

        
        writeFile("roboticarm.csv", List.of(
            "id,active",
            "RA-01,true",
            "RA-02,false",
            "RA-03,true",
            "RA-04,false",
            "RA-05,true"
        ));

        
        writeFile("storagelocation.csv", List.of(
            "id,capacity,currentLoad",
            "S1,100,60",
            "S2,120,85",
            "S3,150,120",
            "S4,200,180",
            "S5,90,30",
            "S6,140,100",
            "S7,160,155",
            "S8,110,70"
        ));

        
        writeFile("task.csv", List.of(
            "id,type,source,destination,status,createdAt",
            "T-1001,MOVE,S1,S2,IN_PROGRESS,2025-11-12 10:00:00",
            "T-1002,LOAD,S2,S3,PENDING,2025-11-12 10:05:00",
            "T-1003,UNLOAD,S3,S1,COMPLETED,2025-11-12 09:50:00",
            "T-1004,CHARGE,ST-02,ST-03,IN_PROGRESS,2025-11-12 10:10:00",
            "T-1005,MOVE,S5,S6,PENDING,2025-11-12 10:12:00",
            "T-1006,LOAD,S4,S7,IN_PROGRESS,2025-11-12 10:15:00",
            "T-1007,UNLOAD,S8,S2,COMPLETED,2025-11-12 09:55:00",
            "T-1008,CHARGE,ST-04,ST-05,COMPLETED,2025-11-12 09:40:00",
            "T-1009,MOVE,S6,S1,PENDING,2025-11-12 10:18:00",
            "T-1010,LOAD,S7,S8,IN_PROGRESS,2025-11-12 10:20:00"
        ));
        
        
        writeFile("medicine.csv", List.of(
        	    "name,batchNumber,quantity",
        	    "Aspirin,BATCH-A1,100",
        	    "Paracetamol,BATCH-P2,200",
        	    "Ibuprofen,BATCH-I3,150",
        	    "Amoxicillin,BATCH-AM4,80",
        	    "Cough Syrup,BATCH-CS5,60"
        	));
        
        writeFile("order.csv", List.of(
        	    "orderId,medicineName,quantity,status,createdAt",
        	    "O-3001,Aspirin,2,CREATED,2025-11-12 09:30:00",
        	    "O-3002,Ibuprofen,1,PROCESSING,2025-11-12 09:45:00",
        	    "O-3003,Cough Syrup,3,COMPLETED,2025-11-12 08:55:00",
        	    "O-3004,Paracetamol,1,CANCELLED,2025-11-12 07:40:00",
        	    "O-3005,Amoxicillin,4,CREATED,2025-11-12 10:10:00"
        	));
        
        

        logger.logInfo("DatabaseGenerator", "CSV databased has been generated: " + OUTPUT_DIR);
    }

    private static void writeFile(String name, List<String> lines) throws IOException {
        Path path = Paths.get(OUTPUT_DIR + name);
        Files.write(path, lines);
        logger.logInfo("DatabaseGenerator", "File creaated: " + path.getFileName());
    }
}
