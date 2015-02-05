package org.limeprotocol.network;

import org.limeprotocol.Envelope;

/**
 * Class responsible for send ordered events
 * Implement TransportListener in order to modulate the code
 */
public interface TransportListenerBroadcastSender {
    void addListener(Transport.TransportListener listener);

    void addListener(Transport.TransportListener listener, Integer priority);

    void removeListener(Transport.TransportListener listener);

    void broadcastOnReceive(Envelope envelope);

    void broadcastOnException(Exception e);
    
    void broadcastOnClosing();

    void broadcastOnClosed();
}
