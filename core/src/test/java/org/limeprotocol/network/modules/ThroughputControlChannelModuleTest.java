package org.limeprotocol.network.modules;

import com.google.common.base.Stopwatch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.limeprotocol.EnvelopeId;
import org.limeprotocol.Message;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.testHelpers.Dummy;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by aldo on 4/18/2016.
 */
public class ThroughputControlChannelModuleTest {

    @Mock
    private ClientChannel channel;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    private ThroughputControlChannelModule getTarget(int throughput){
        return ThroughputControlChannelModule.createAndRegister(channel, throughput);
    }

    @Test
    public void onSending_shouldSendAllMessages_respectingThroughput() {

        int totalMessages = 50;

        // Arrange
        Message[] messages = new Message[totalMessages];
        for (int i = 0; i < totalMessages; i++) {
            Message message = Dummy.createMessage(Dummy.createTextContent());
            message.setId(EnvelopeId.newId());
            messages[i] = message;
        }
        ThroughputControlChannelModule target = getTarget(10);

        //Act
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < totalMessages; i++){
            target.onSending(messages[i]);
        }
        stopwatch.stop();

        long totalMinutes = stopwatch.elapsed(TimeUnit.MINUTES);
        long totalSeconds = stopwatch.elapsed(TimeUnit.SECONDS);
        long totalMiliseconds = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        //Assert
        Assert.assertTrue(stopwatch.elapsed(TimeUnit.MILLISECONDS) >= 3950);
        Assert.assertTrue(stopwatch.elapsed(TimeUnit.MILLISECONDS) < 4050);
    }
}
