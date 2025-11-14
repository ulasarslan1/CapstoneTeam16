package ChargingStation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChargingQueue {
    private BlockingQueue<AGV> queue;

    public ChargingQueue() {
        queue = new LinkedBlockingQueue<>();
    }

    
    public void add(AGV agv) {
        queue.offer(agv);
    }

    
    public AGV poll() {
        return queue.poll();
    }

    
    public AGV peek() {
        return queue.peek();
    }

    
    public int size() {
        return queue.size();
    }

    
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public BlockingQueue<AGV> getQueue() {
        return queue;
    }
}

