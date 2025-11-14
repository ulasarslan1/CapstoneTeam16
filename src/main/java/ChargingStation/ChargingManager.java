package ChargingStation;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import Logging.SystemLogger;

/**
 * ChargingManager manages a pool of charging stations and queues AGVs.
 * Station IDs are defined externally (no auto-generation).
 * Thread-safe and concurrent.
 */
public class ChargingManager {

    private final List<String> stationIds;
    private final ChargingQueue queue;
    private final ExecutorService executor;
    private final Semaphore slots;
    private final long dropThresholdSeconds;
    private final AtomicBoolean running;
    private final SystemLogger logger;

    
    public ChargingManager(List<String> stationIds, long dropThresholdSeconds) {
        if (stationIds == null || stationIds.isEmpty()) {
            throw new IllegalArgumentException("Station IDs cannot be null or empty.");
        }

        this.stationIds = new ArrayList<>(stationIds);
        this.queue = new ChargingQueue();
        this.executor = Executors.newFixedThreadPool(stationIds.size());
        this.slots = new Semaphore(stationIds.size());
        this.dropThresholdSeconds = dropThresholdSeconds;
        this.running = new AtomicBoolean(false);
        this.logger = new SystemLogger();

        logger.logInfo("ChargingManager", "Initialized with " + stationIds.size() + " stations.");
    }

    
    public void submitAGV(AGV agv) {
        if (agv == null) return;

        queue.add(agv);
        logger.logInfo("ChargingManager", "AGV " + agv.getId() + " added to queue.");

        if (running.compareAndSet(false, true)) {
            executor.submit(this::processQueue);
        }
    }

    
    private void processQueue() {
        try {
            Iterator<String> stationIterator = stationIds.iterator();

            while (!queue.isEmpty()) {
                AGV candidate = queue.peek();
                if (candidate == null) break;

                long waited = (System.currentTimeMillis() - candidate.getArrivalTime()) / 1000;
                if (waited > dropThresholdSeconds) {
                    logger.logWarning("ChargingManager", "AGV " + candidate.getId() + " dropped due to timeout.");
                    queue.poll();
                    continue;
                }

                if (slots.tryAcquire(300, TimeUnit.MILLISECONDS)) {
                    AGV agv = queue.poll();
                    if (agv == null) {
                        slots.release();
                        continue;
                    }

                    String stationId;
                    synchronized (stationIds) {
                        if (!stationIterator.hasNext()) {
                            stationIterator = stationIds.iterator();
                        }
                        stationId = stationIterator.next();
                    }

                    logger.logInfo("ChargingManager", "Assigning AGV " + agv.getId() + " to " + stationId);

                    executor.submit(() -> {
                        try {
                            ChargingStation station = new ChargingStation(stationId, agv);
                            station.run();
                        } catch (Exception e) {
                            logger.logError("ChargingManager", "Error while charging AGV " + agv.getId() + ": " + e.getMessage());
                        } finally {
                            slots.release();
                        }
                    });
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.logError("ChargingManager", "Interrupted while processing queue.");
        } finally {
            running.set(false);
        }
    }

    public ChargingQueue getQueue() {
        return queue;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void shutdown() {
        logger.logInfo("ChargingManager", "Shutting down...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.logInfo("ChargingManager", "Stopped.");
    }
}
