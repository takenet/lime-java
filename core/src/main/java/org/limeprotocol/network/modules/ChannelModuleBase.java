package org.limeprotocol.network.modules;

import org.limeprotocol.Envelope;
import org.limeprotocol.Session;
import org.limeprotocol.network.ChannelModule;

/**
 * Wrapper abstract class to provide an empty implementation of the ChannelModule interface.
 * @param <T>
 */
public abstract class ChannelModuleBase<T extends Envelope> implements ChannelModule<T> {

    @Override
    public void onStateChanged(Session.SessionState state) {

    }

    @Override
    public T onReceiving(T envelope) {
        return envelope;
    }

    @Override
    public T onSending(T envelope) {
        return envelope;
    }
}
