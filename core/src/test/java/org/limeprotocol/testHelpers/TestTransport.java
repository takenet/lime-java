package org.limeprotocol.testHelpers;

import org.limeprotocol.Envelope;
import org.limeprotocol.Session;
import org.limeprotocol.network.TransportBase;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.Queue;

public class TestTransport extends TransportBase {
        private Queue<Envelope> envelopeQueue;

        public TestTransport() {
                envelopeQueue = new LinkedList<>();
        }

        @Override
        protected void performClose() throws IOException {

        }

        @Override
        public void send(Envelope envelope) throws IOException {
                raiseOnReceive(envelopeQueue.poll());
        }

        @Override
        public void open(URI uri) throws IOException {

        }

        public void addNextEnvelopeToReturn(Envelope envelope) {
                envelopeQueue.add(envelope);
        }
}
