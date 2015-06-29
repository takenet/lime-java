package org.limeprotocol.network;

import org.limeprotocol.Envelope;
import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;

import java.io.IOException;
import java.util.*;

/**
 *  Base class for transport implementation.
 */
public abstract class TransportBase implements Transport {
    
    private SessionCompression compression;
    private SessionEncryption encryption;
    private TransportEnvelopeListener transportEnvelopeListener;
    private TransportStateListener transportStateListener;
    private boolean closingInvoked;
    private boolean closedInvoked;

    protected TransportBase() {
        compression = SessionCompression.NONE;
        encryption = SessionEncryption.NONE;
    }

    @Override
    public void setEnvelopeListener(TransportEnvelopeListener listener) {
        this.transportEnvelopeListener = listener;
    }

    @Override
    public void setStateListener(TransportStateListener listener) {
        this.transportStateListener = listener;
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

    protected TransportEnvelopeListener getEnvelopeListener() {
        return transportEnvelopeListener;
    }

    protected TransportStateListener getStateListener() {
        return transportStateListener;
    }
    
    protected void raiseOnReceive(Envelope envelope) {
        TransportEnvelopeListener listener = getEnvelopeListener();
        if (listener != null) {
            listener.onReceive(envelope);
        } else {
            System.out.println("An envelope was received while there's no listener registered - Id: " + envelope.getId() + " - Type: " + envelope.getClass().getSimpleName());
        }
    }

    protected void raiseOnException(Exception e) {
        TransportStateListener listener = getStateListener();
        if (listener != null) {
            listener.onException(e);
        } else {
            System.out.println("An transport exception was received while there's no listener registered: " + e.toString());
        }
    }

    protected void raiseOnClosing() {
        TransportStateListener listener = getStateListener();
        if (listener != null) {
            listener.onClosing();
        } else {
            System.out.println("The transport is about to be closed while there's no listener registered");
        }
    }

    protected void raiseOnClosed() {
        TransportStateListener listener = getStateListener();
        if (listener != null) {
            listener.onClosed();
        } else {
            System.out.println("The transport was closed while there's no listener registered");
        }
    }
}