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
    private TransportListener transportListener;
    private boolean closingInvoked;
    private boolean closedInvoked;

    protected TransportBase() {
        compression = SessionCompression.NONE;
        encryption = SessionEncryption.NONE;
    }
    
    @Override
    public void setListener(TransportListener listener) {
        this.transportListener = listener;
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
        TransportListener listener = this.transportListener;
        if (listener != null) {
            listener.onReceive(envelope);
        }
    }

    protected void raiseOnException(Exception e) {
        TransportListener listener = this.transportListener;
        if (listener != null) {
            listener.onException(e);
        }
    }

    protected TransportListener getListener() {
        return transportListener;
    }

    protected void raiseOnClosing() {
        TransportListener listener = this.transportListener;
        if (listener != null) {
            listener.onClosing();
        }
    }

    protected void raiseOnClosed() {
        TransportListener listener = this.transportListener;
        if (listener != null) {
            listener.onClosed();
        }
    }
    
}