package org.limeprotocol.network.modules;

import org.limeprotocol.Envelope;
import org.limeprotocol.network.Channel;
import org.limeprotocol.util.RateGate;

import java.util.concurrent.TimeUnit;

public class ThroughputControlChannelModule extends ChannelModuleBase {

    private RateGate rateGate;
    private long timeoutSec = 10;

    private ThroughputControlChannelModule(int throughput) {
        rateGate = new RateGate(throughput);
    }

    @Override
    public Envelope onSending(Envelope envelope) {
        try {
            rateGate.waitToProceed(timeoutSec, TimeUnit.SECONDS);
        }catch (InterruptedException e){ }
        return envelope;
    }

    public static ThroughputControlChannelModule createAndRegister(Channel channel) {
        return createAndRegister(channel, 10);
    }

    public static ThroughputControlChannelModule createAndRegister(Channel channel, int throughput) {
        ThroughputControlChannelModule throughputControlChannelModule = new ThroughputControlChannelModule(throughput);
        channel.getMessageModules().add(throughputControlChannelModule);
        channel.getCommandModules().add(throughputControlChannelModule);
        channel.getNotificationModules().add(throughputControlChannelModule);
        return throughputControlChannelModule;
    }
}
