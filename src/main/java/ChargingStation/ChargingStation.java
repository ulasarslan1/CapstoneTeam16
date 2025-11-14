package ChargingStation;

import Exceptions.ChargingException;
import Logging.SystemLogger;

public class ChargingStation implements Runnable {
    private String stationId;
    private AGV agv;
    private SystemLogger logger;

    public ChargingStation(String stationId, AGV agv) {
        this.stationId = stationId;
        this.agv = agv;
        this.logger = new SystemLogger();
    }

    @Override
    public void run() {
        try {
            logger.logInfo("Station-" + stationId, "Starting charging for AGV-" + agv.getId());
            if (Math.random() < 0.02) {
                throw new ChargingException(
                    "Hardware failure at station " + stationId,
                    ChargingException.ChargingErrorType.MALFUNCTION
                );
            }
            agv.charge();
            logger.logInfo("Station-" + stationId, "Finished charging" + agv.getId());
        } catch (ChargingException e) {
            logger.logError("Station-" + stationId, "Charging error: " + e.getErrorType());
        } catch (Exception e) {
            logger.logError("Station-" + stationId, "Unexpected error: " + e.getMessage());
        }
    }

	public String getStationId() {
		return stationId;
	}

	
}
