package ChargingStation;

import org.junit.jupiter.api.Test;
import java.util.*;
import Exceptions.ChargingException;

public class ChargingSimulationTest {

    @Test
    public void testAGVChargingSimulation() throws InterruptedException {
        List<String> stationIds = Arrays.asList("ST-01", "ST-02");
        ChargingManager manager = new ChargingManager(stationIds, 15);

        // Simulate 5 AGVs
        for (int i = 0; i < 5; i++) {
            boolean urgent = (i % 2 == 0);
            int battery = (int) (Math.random() * 50);
            AGV agv = new AGV("AGV-" + (i + 1), battery, urgent);
            manager.submitAGV(agv);
        }

        // Simulate extra arriving AGVs after 1 second
        Thread.sleep(1000);
        for (int i = 0; i < 5; i++) {
            AGV agv = new AGV("AGV-X" + (i + 1), (int) (Math.random() * 30), false);
            manager.submitAGV(agv);
        }

        Thread.sleep(5000);
        manager.shutdown();
    }

    @Test
    public void testChargeZeroShouldThrowException() {
        AGV agv = new AGV("AGV-ERR", 0, false);
        try {
            agv.charge();
        } catch (ChargingException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
    }

    @Test
    public void testStationMalfunctionHandling() throws InterruptedException {
        List<String> stationIds = Arrays.asList("ST-01", "ST-02");
        ChargingManager manager = new ChargingManager(stationIds, 15);
        AGV agv = new AGV("AGV-TEST", 40, false);
        manager.submitAGV(agv);

        Thread.sleep(2000);
        manager.shutdown();
    }
}
