package org.limeprotocol.util;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by aldo on 4/19/2016.
 */
public class RateGate {

    private DelayQueue<QueueElement> delayQueue;

    public RateGate(int occurrences) {
        delayQueue = new DelayQueue<>();
        for (int i = 0; i < occurrences; i++){
            delayQueue.add(new QueueElement(0));
        }
    }

    public void waitToProceed(long timeout, TimeUnit unit) throws InterruptedException {
        delayQueue.poll(timeout, unit);
        delayQueue.add(new QueueElement(1000));
    }

    private class QueueElement implements Delayed{

        private long expirationTimeMilisec;

        public QueueElement(long delayMilisec){
            this.expirationTimeMilisec = System.currentTimeMillis() + delayMilisec;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(expirationTimeMilisec - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            if (this.expirationTimeMilisec < ((QueueElement) o).expirationTimeMilisec) {
                return -1;
            }
            if (this.expirationTimeMilisec > ((QueueElement) o).expirationTimeMilisec) {
                return 1;
            }
            return 0;
        }
    }
}
