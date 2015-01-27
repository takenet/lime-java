package org.limeprotocol.network;

import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;

import java.io.IOException;
import java.util.Arrays;

/**
 *  Base class for transport implementation.
 */
public abstract class TransportBase implements Transport {
    
    private TransportListenerBroadcastSender transportListenerBroadcastSender;
    private boolean closingInvoked;
    private boolean closedInvoked;

    private SessionCompression compression;
    private SessionEncryption encryption;
    
    protected TransportBase() {
        compression = SessionCompression.none;
        encryption = SessionEncryption.none;
        //TODO use dependency injection ?
        transportListenerBroadcastSender = new TransportListenerBroadcastSenderImpl();
    }

    /**
     * Closes the transport.
     */
    protected abstract void performClose() throws IOException;


    @Override
    public void addListener(TransportListener transportListener) {
        transportListenerBroadcastSender.addListener(transportListener);
    }

    @Override
    public void addListener(TransportListener transportListener, Integer priority) {
        transportListenerBroadcastSender.addListener(transportListener, priority);
    }

    protected TransportListenerBroadcastSender getListenerBroadcastSender() {
        return transportListenerBroadcastSender;
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
        if (Arrays.asList(getSupportedCompression()).contains(compression)) {
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
        if (Arrays.asList(getSupportedEncryption()).contains(encryption)) {
            throw new IllegalArgumentException("encryption");
        }
        this.encryption = encryption;
    }

    @Override
    public void close() throws IOException {
        if (!closingInvoked) {
            transportListenerBroadcastSender.broadcastOnClosing();
            closingInvoked = true;
        }
        performClose();
        if (!closedInvoked) {
            transportListenerBroadcastSender.broadcastOnClosed();
            closedInvoked = true;
        }
    }
}