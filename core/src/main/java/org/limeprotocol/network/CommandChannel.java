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
     * @param listener
     * @param removeAfterReceive
     */
    void addCommandListener(CommandChannelListener listener, boolean removeAfterReceive);

    /**
     * Removes the specified listener.
     * @param listener
     */
    void removeCommandListener(CommandChannelListener listener);

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
