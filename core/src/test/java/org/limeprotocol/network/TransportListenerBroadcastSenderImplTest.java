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
        int onExceptionCount;

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

        /**
         * Occurs when an exception is thrown
         * during the receive process.
         *
         * @param e The thrown exception.
         */
        @Override
        public void onException(Exception e) {
            onExceptionCount++;
        }

        /**
         * Indicates if the listener is active.
         *
         * @return
         */
        @Override
        public boolean isListening() {
            return true;
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
        assertEquals(listener1.onReceiveCount, 1);
        assertEquals(listener2.onReceiveCount, 1);
    }

    @Test
    public void listenerAreCalledInTheCorrectOrder(){
        listenerBroadcastSender.addListener(listener1, 5);

        Transport.TransportListener verifyListener = new Transport.TransportListener() {
            @Override
            public void onReceive(Envelope envelope) {
                assertEquals(listener1.onReceiveCount, 1);
            }

            @Override
            public void onClosing() {

            }

            @Override
            public void onClosed() {

            }

            @Override
            public void onException(Exception e) {

            }

            /**
             * Indicates if the listener is active.
             *
             * @return
             */
            @Override
            public boolean isListening() {
                return false;
            }
        };

        listenerBroadcastSender.addListener(verifyListener, 5);
        listenerBroadcastSender.broadcastOnReceive(null);
    }
}
