package org.limeprotocol.network;

import org.limeprotocol.Envelope;
import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *  Base class for transport implementation.
 */
public abstract class TransportBase implements Transport {
    
    private SessionCompression compression;
    private SessionEncryption encryption;
    private Set<TransportListener> transportListeners;
    private boolean closingInvoked;
    private boolean closedInvoked;

    protected TransportBase() {
        compression = SessionCompression.none;
        encryption = SessionEncryption.none;
        transportListeners = new HashSet<>();
    }
    
    @Override
    public void addListener(TransportListener transportListener) {
        if (transportListener == null) {
            throw new IllegalArgumentException("transportListener");
        }
        transportListeners.add(transportListener);
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

    protected void raiseOnReceive(Envelope envelope) {
        for (TransportListener transportListener : transportListeners) {
            transportListener.onReceive(envelope);
        }
        removeInactiveListeners();
    }

    protected void raiseOnException(Exception e) {
        for (TransportListener transportListener : transportListeners) {
            transportListener.onException(e);
        }
        removeInactiveListeners();
    }

    protected boolean hasAnyListener() {
        return !transportListeners.isEmpty();
    }

    private void raiseOnClosing() {
        for (TransportListener transportListener : transportListeners) {
            transportListener.onClosing();
        }
        removeInactiveListeners();
    }

    private void raiseOnClosed() {
        for (TransportListener transportListener : transportListeners) {
            transportListener.onClosed();
        }
        removeInactiveListeners();
    }
    
    private void removeInactiveListeners() {
        Set<TransportListener> inactiveListeners = new HashSet<TransportListener>();

        for (TransportListener transportListener : transportListeners) {
            if (!transportListener.isActive()) {
                inactiveListeners.add(transportListener);
            }
        }

        for (TransportListener inactiveListener : inactiveListeners) {
            transportListeners.remove(inactiveListener);
        }
    }
}