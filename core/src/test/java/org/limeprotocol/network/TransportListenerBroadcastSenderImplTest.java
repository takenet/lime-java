package org.limeprotocol.network;

import org.junit.Before;
import org.junit.Test;
import org.limeprotocol.Envelope;

import static org.junit.Assert.assertEquals;

public class TransportListenerBroadcastSenderImplTest {

    private class DummyTransportListener implements Transport.TransportListener{

        int onReceiveCount;
        int onClosingCount;
        int onClosedCount;

        @Override
        public void onReceive(Envelope envelope) {
            onReceiveCount++;
        }

        @Override
        public void onClosing() {
            onClosingCount++;
        }

        @Override
        public void onClosed() {
            onClosedCount++;
        }
    }

    private DummyTransportListener listener1;
    private DummyTransportListener listener2;
    private TransportListenerBroadcastSender listenerBroadcastSender;

    @Before
    public void setUp(){
        listener1 = new DummyTransportListener();
        listener2 = new DummyTransportListener();
        listenerBroadcastSender = new TransportListenerBroadcastSenderImpl();
    }

    @Test
    public void bothListenerAreCalled(){
        listenerBroadcastSender.addListener(listener1);
        listenerBroadcastSender.addListener(listener2);
        listenerBroadcastSender.broadcastOnReceive(null);
        assertEquals(1, listener1.onReceiveCount);
        assertEquals(1, listener2.onReceiveCount);
    }

    @Test
    public void listenerAreCalledInTheCorrectOrder(){
        listenerBroadcastSender.addListener(listener1, 1);

        Transport.TransportListener verifyListener = new Transport.TransportListener() {
            @Override
            public void onReceive(Envelope envelope) {
                assertEquals(1, listener1.onReceiveCount);
            }

            @Override
            public void onClosing() {

            }

            @Override
            public void onClosed() {

            }
        };

        listenerBroadcastSender.addListener(verifyListener, 5);
        listenerBroadcastSender.broadcastOnReceive(null);
    }
}
