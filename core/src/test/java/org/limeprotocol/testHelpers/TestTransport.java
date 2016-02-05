package org.limeprotocol.testHelpers;

import com.google.common.collect.Iterators;
import org.limeprotocol.Envelope;
import org.limeprotocol.network.TransportBase;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TestTransport extends TransportBase {
    private List<Envelope> sentEnvelopes;
    private Queue<Envelope> outgoingEnvelopes;
    private boolean isClosed;

    public TestTransport() {
        outgoingEnvelopes = new LinkedList<>();
        sentEnvelopes = new ArrayList<>();
    }

    @Override
    protected void performClose() throws IOException {

    }

    @Override
    protected void performOpen(URI uri) throws IOException {

    }

    @Override
    public void send(Envelope envelope) throws IOException {
        sentEnvelopes.add(envelope);
        if (outgoingEnvelopes.size() != 0) {
            raiseOnReceive(outgoingEnvelopes.poll());
        }
    }

    @Override
    public void open(URI uri) throws IOException {

    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public synchronized void close() {
        try {
            super.close();
            isClosed = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNextEnvelopeToReturn(Envelope envelope) {
        outgoingEnvelopes.add(envelope);
    }

    public Envelope[] getSentEnvelopes() {
        return Iterators.toArray(sentEnvelopes.iterator(), Envelope.class);
    }

    public boolean isClosed() {
        return isClosed;
    }
}
