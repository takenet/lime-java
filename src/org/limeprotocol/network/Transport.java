package org.limeprotocol.network;

import org.limeprotocol.Envelope;

/**
 *
 */
public interface Transport {


    /**
     *
     * @param envelope
     */
    void send(Envelope envelope);

    /**
     * Sets the transport listener for receiving envelopes.
     * @param transportListener
     */
    void setListener(TransportListener transportListener);

    /**
     *
     */
    public interface TransportListener
    {
        void onReceive(Envelope envelope);
    }
}