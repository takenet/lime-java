package org.limeprotocol.network;

import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;

import java.io.IOException;

public abstract class TransportBase implements Transport {
    
    private TransportListener transportListener;

    protected TransportBase() {

    }
    protected abstract void performClose();


    @Override
    public void setListener(TransportListener transportListener) {
        this.transportListener = transportListener;
    }


    @Override
    public SessionCompression[] getSupportedCompression() {
        return new SessionCompression[] { getCompression() };
    }

    @Override
    public SessionCompression getCompression() {
        return SessionCompression.none;
    }

    @Override
    public void setCompression(SessionCompression compression) throws IOException  {
        if (compression != getCompression()) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public SessionEncryption[] getSupportedEncryption() {
        return new SessionEncryption[] { getEncryption() };
    }

    @Override
    public SessionEncryption getEncryption() {
        return SessionEncryption.none;
    }

    @Override
    public void setEncryption(SessionEncryption encryption) throws IOException {
        if (encryption != getEncryption()) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void close() {
        TransportListener transportListener = this.transportListener;
        if (transportListener != null) {
            transportListener.onClosing();
        }
        performClose();
        if (transportListener != null) {
            transportListener.onClosed();
        }
    }
}
