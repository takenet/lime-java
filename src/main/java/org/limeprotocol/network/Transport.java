package org.limeprotocol.network;

import org.limeprotocol.Envelope;

public interface Transport {


    void send(Envelope envelope);

    void addListener(TransportListener transportListener);

    public interface TransportListener
    {
        void onReceive(Envelope envelope);
    }
}
