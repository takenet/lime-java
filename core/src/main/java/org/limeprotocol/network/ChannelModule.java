package org.limeprotocol.network;

import org.limeprotocol.Envelope;
import org.limeprotocol.Session;

public interface ChannelModule<T extends Envelope> {
    void onStateChanged(Session.SessionState state);

    T onSending(T envelope);

    T onReceiving(T envelope);
}
