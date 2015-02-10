package org.limeprotocol.testHelpers;

import com.google.common.collect.Iterators;
import org.limeprotocol.Envelope;
import org.limeprotocol.network.TransportBase;

import java.io.IOException;
import java.net.URI;
import java.util.*;

public class TestTransport extends TransportBase {
        private List<Envelope> sentEnvelopes;
        private Queue<Envelope> outgoingEnvelopes;

        public TestTransport() {
                outgoingEnvelopes = new LinkedList<>();
                sentEnvelopes = new ArrayList<>();
        }

        @Override
        protected void performClose() throws IOException {

        }

        @Override
        public void send(Envelope envelope) throws IOException {
                sentEnvelopes.add(envelope);
                raiseOnReceive(outgoingEnvelopes.poll());
        }

        @Override
        public void open(URI uri) throws IOException {

        }

        public void addNextEnvelopeToReturn(Envelope envelope) {
                outgoingEnvelopes.add(envelope);
        }

        public Envelope[] getSentEnvelopes() {
                return Iterators.toArray(sentEnvelopes.iterator(), Envelope.class);
        }
}
