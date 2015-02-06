package org.limeprotocol.network;

import org.limeprotocol.Command;

import java.io.IOException;

/**
 * Defines a command envelopes exchanging channel.
 */
public interface CommandChannel {
    /**
     * Sends a command to the remote node.
     * @param command
     */
    void sendCommand(Command command) throws IOException;

    /**
     * Sets the listener for receiving commands.
     * @param commandChannelListener
     */
    void setCommandChannelListener(CommandChannelListener commandChannelListener);

    /**
     * Defines a command channel listener.
     */
    public interface CommandChannelListener {
        /**
         * Occurs when a command is received by the channel.
         * @param command
         */
        void onReceiveCommand(Command command);
    }
}
