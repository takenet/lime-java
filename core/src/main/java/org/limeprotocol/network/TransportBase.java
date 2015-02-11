package org.limeprotocol.network;

import org.limeprotocol.Envelope;
import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;

import java.io.IOException;
import java.util.*;
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
    public synchronized void addListener(TransportListener listener, boolean removeAfterReceive) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }

        if (!singleReceiveTransportListeners.contains(listener) &&
                !transportListeners.contains(listener)) {
            if (removeAfterReceive) {
                singleReceiveTransportListeners.add(listener);
            } else {
                transportListeners.add(listener);
            }
        }
    }

    
    @Override
    public synchronized void removeListener(TransportListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }

        if (!transportListeners.remove(listener)) {
            singleReceiveTransportListeners.remove(listener);
        }
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

    protected void raiseOnReceive(Envelope envelope) {
        for (TransportListener listener : getInvocationListeners()) {
            listener.onReceive(envelope);
        }
    }

    protected void raiseOnException(Exception e) {
        for (TransportListener listener : getInvocationListeners()) {
            listener.onException(e);
        }
    }

    protected synchronized boolean hasAnyListener() {
        return !(transportListeners.isEmpty() && singleReceiveTransportListeners.isEmpty());
    }

    protected void raiseOnClosing() {
        for (TransportListener listener : getInvocationListeners()) {
            listener.onClosing();
        }
    }

    protected void raiseOnClosed() {
        for (TransportListener listener : getInvocationListeners()) {
            listener.onClosed();
        }
    }

    private synchronized List<TransportListener> getInvocationListeners() {
        List<TransportListener> invocationListeners = new ArrayList<>(transportListeners);
        while (!singleReceiveTransportListeners.isEmpty()) {
            invocationListeners.add(singleReceiveTransportListeners.remove());
        }
        return invocationListeners;
    }
}