package org.limeprotocol.network;

import org.limeprotocol.*;

import java.io.IOException;
import java.util.Collection;

/**
 * Defines a communication channel for the protocol.
 */
public interface Channel extends MessageChannel, CommandChannel, NotificationChannel, SessionChannel {
    /**
     * Gets the current session transport
     * @return
     */
    Transport getTransport();

    /**
     * Gets the remote node identifier.
     * @return
     */
    Node getRemoteNode();

    /**
     * Gets the local node identifier.
     * @return
     */
    Node getLocalNode();

    /**
     * Gets the current session Id.
     * @return
     */
    String getSessionId();

    /**
     * Gets the current session state.
     * @return
     */
    Session.SessionState getState();

    /**
     * Gets the message modules for processing sent and received messages.
     * @return
     */
    Collection<ChannelModule<Message>> getMessageModules();

    /**
     * Gets the message modules for processing sent and received notifications.
     * @return
     */
    Collection<ChannelModule<Notification>> getNotificationModules();

    /**
     * Gets the message modules for processing sent and received commands.
     * @return
     */
    Collection<ChannelModule<Command>> getCommandModules();

    /**
     * Sends the envelope using the appropriate method for its type.
     *
     * @param channel
     * @param envelope
     * @throws IOException
     */
    default void send(Channel channel, Envelope envelope) throws IOException {
        if (channel == null) {
            throw new IllegalArgumentException("channel");
        }
        if (envelope == null) {
            throw new IllegalArgumentException("envelope");
        }
        if (envelope instanceof Notification) {
            channel.sendNotification((Notification) envelope);
        } else if (envelope instanceof Message) {
            channel.sendMessage((Message) envelope);
        } else if (envelope instanceof Command) {
            channel.sendCommand((Command) envelope);
        } else if (envelope instanceof Session) {
            channel.sendSession((Session) envelope);
        } else {
            throw new IllegalArgumentException("Invalid or unknown envelope type");
        }
    }
}
