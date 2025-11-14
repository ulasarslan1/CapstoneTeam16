package ChargingStation;

import Exceptions.ChargingException;


public class AGV implements Comparable<AGV> { 
    private String id;
    private int batteryLevel;
    private boolean urgentOrder;
    private long arrivalTime;

    public AGV(String id, int batteryLevel, boolean urgentOrder) {
        this.id = id;
        this.batteryLevel = batteryLevel;
        this.urgentOrder = urgentOrder;
        this.arrivalTime = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public int getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(int b) { this.batteryLevel = b; }
    public boolean isUrgent() { return urgentOrder; }
    public long getArrivalTime() { return arrivalTime; }

    // Simulate charging action
    public void charge() throws ChargingException {
        try {
            // simulate possible malfunction randomly
            if (Math.random() < 0.05) {
                throw new ChargingException("Random malfunction", ChargingException.ChargingErrorType.MALFUNCTION);
            }
            // simulate time taken to charge
            Thread.sleep(500 + (int)(Math.random()*1000));
            this.batteryLevel = 100;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ChargingException("Charging interrupted for AGV-" + id, ChargingException.ChargingErrorType.CONNECTION_LOST);
        }
    }

    @Override
    public int compareTo(AGV other) {
        if (this.urgentOrder && !other.urgentOrder) return -1;
        if (!this.urgentOrder && other.urgentOrder) return 1;
        return Long.compare(this.arrivalTime, other.arrivalTime);
    }
}
