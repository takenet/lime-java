package org.limeprotocol.network;

import org.limeprotocol.Envelope;
import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *  Base class for transport implementation.
 */
public abstract class TransportBase implements Transport {
    
    private SessionCompression compression;
    private SessionEncryption encryption;
    private Set<TransportListener> transportListeners;
    private final Queue<TransportListener> singleReceiveTransportListeners;
    private boolean closingInvoked;
    private boolean closedInvoked;

    protected TransportBase() {
        compression = SessionCompression.NONE;
        encryption = SessionEncryption.NONE;
        transportListeners = new HashSet<>();
        singleReceiveTransportListeners = new LinkedBlockingQueue<>();
    }
    
    @Override
    public void addListener(TransportListener transportListener, boolean removeAfterReceive) {
        if (transportListener == null) {
            throw new IllegalArgumentException("transportListener");
        }
        if (removeAfterReceive) {
            singleReceiveTransportListeners.add(transportListener);
        } else {
            transportListeners.add(transportListener);
        }
    }
    
    @Override
    public void removeListener(TransportListener transportListener) {
        if (transportListener == null) {
            throw new IllegalArgumentException("transportListener");
        }
        transportListeners.remove(transportListener);
    }
    
    @Override
    public SessionCompression[] getSupportedCompression() {
        return new SessionCompression[] { getCompression() };
    }

    @Override
    public SessionCompression getCompression() {
        return compression;
    }

    @Override
    public void setCompression(SessionCompression compression) throws IOException  {
        if (!Arrays.asList(getSupportedCompression()).contains(compression)) {
            throw new IllegalArgumentException("compression");
        }
        this.compression = compression;
    }

    @Override
    public SessionEncryption[] getSupportedEncryption() {
        return new SessionEncryption[] { getEncryption() };
    }

    @Override
    public SessionEncryption getEncryption() {
        return encryption;
    }

    @Override
    public void setEncryption(SessionEncryption encryption) throws IOException {
        if (!Arrays.asList(getSupportedEncryption()).contains(encryption)) {
            throw new IllegalArgumentException("encryption");
        }
        this.encryption = encryption;
    }

    @Override
    public synchronized void close() throws IOException {
        if (!closingInvoked) {
            raiseOnClosing();
            closingInvoked = true;
        }
        performClose();
        if (!closedInvoked) {
            raiseOnClosed();
            closedInvoked = true;
        }
    }
    
    /**
     * Closes the transport.
     */
    protected abstract void performClose() throws IOException;

    protected synchronized void raiseOnReceive(Envelope envelope) {
        for (TransportListener listener : transportListeners) {
            listener.onReceive(envelope);
        }
        while (!singleReceiveTransportListeners.isEmpty()) {
            TransportListener listener = singleReceiveTransportListeners.remove();
            listener.onReceive(envelope);
        }
    }

    protected synchronized void raiseOnException(Exception e) {
        for (TransportListener listener : transportListeners) {
            listener.onException(e);
        }
        for (TransportListener listener : singleReceiveTransportListeners) {
            listener.onClosing();
        }
    }

    protected boolean hasAnyListener() {
        return !(transportListeners.isEmpty() && singleReceiveTransportListeners.isEmpty());
    }

    private synchronized void raiseOnClosing() {
        for (TransportListener listener : transportListeners) {
            listener.onClosing();
        }
        for (TransportListener listener : singleReceiveTransportListeners) {
            listener.onClosing();
        }
    }

    private synchronized void raiseOnClosed() {
        for (TransportListener listener : transportListeners) {
            listener.onClosed();
        }
        for (TransportListener listener : singleReceiveTransportListeners) {
            listener.onClosed();
        }
        singleReceiveTransportListeners.clear();
    }
}