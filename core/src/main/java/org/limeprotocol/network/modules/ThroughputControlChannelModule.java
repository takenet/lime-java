package org.limeprotocol.network.modules;

import org.isomorphism.util.TokenBucket;
import org.isomorphism.util.TokenBuckets;
import org.limeprotocol.Envelope;
import org.limeprotocol.network.Channel;

import java.util.concurrent.TimeUnit;

public class ThroughputControlChannelModule extends ChannelModuleBase {

    private TokenBucket tokenBucket;

    private ThroughputControlChannelModule(int throughput) {

        TokenBuckets.Builder builder = TokenBuckets.builder()
                .withCapacity(throughput);

        if(throughput % 10 == 0){
            builder.withFixedIntervalRefillStrategy(throughput/10, 100, TimeUnit.MILLISECONDS);
        } else if(throughput % 5 == 0) {
            builder.withFixedIntervalRefillStrategy(throughput/5, 200, TimeUnit.MILLISECONDS);
        } else {
            builder.withFixedIntervalRefillStrategy(throughput, 1, TimeUnit.SECONDS);
        }

        this.tokenBucket = builder.build();
    }

    @Override
    public Envelope onSending(Envelope envelope) {
        tokenBucket.consume();
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
