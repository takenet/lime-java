package org.limeprotocol.util;

import com.google.common.base.Stopwatch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RateGateTest {
    private RateGate target;
    private final int throughput = 10;


    @Before
    public void setUp() throws Exception {
        target = new RateGate(throughput);
    }

    @Test
    public void waitToProceed_multipleThreads(){
        final int totalThreads = 5;
        final int workPerThread = 10;
        final int rateGateTimeout = 10;
        final long executorServiceTimeout = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);

        Stopwatch stopwatch = Stopwatch.createStarted();

        for(int i = 0; i < totalThreads; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        for(int i = 0; i < workPerThread; i++) {
                            target.waitToProceed(rateGateTimeout, TimeUnit.SECONDS);
                        }
                    }catch (InterruptedException e){
                        Assert.assertTrue(false);
                    }
                }
            });
        }

        executorService.shutdown();

        try {
            executorService.awaitTermination(executorServiceTimeout, TimeUnit.SECONDS);
        }catch (InterruptedException e){ Assert.assertTrue(false); }

        stopwatch.stop();

        Assert.assertTrue(stopwatch.elapsed(TimeUnit.MILLISECONDS) >= 3950);
        Assert.assertTrue(stopwatch.elapsed(TimeUnit.MILLISECONDS) < 4050);
    }
}
