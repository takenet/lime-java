package org.limeprotocol.network;

import org.limeprotocol.*;

import java.io.IOException;
import java.util.UUID;

public class ChannelImpl implements Channel {
    
    private final Transport transport;
    private Node remoteNode;
    private Node localNode;
    private UUID sessionId;
    private Session.SessionState state;
    private CommandChannelListener commandChannelListener;
    private MessageChannelListener messageChannelListener;
    private NotificationChannelListener notificationChannelListener;
    private SessionChannelListener sessionChannelListener;
    private ChannelListener channelListener;

    protected ChannelImpl(Transport transport) {
        if (transport == null) {
            throw new IllegalArgumentException("transport");
        }
        
        this.transport = transport;
        this.state = Session.SessionState.NEW;
    }

    /**
     * Gets the current session transport
     *
     * @return
     */
    @Override
    public Transport getTransport() {
        return transport;
    }

    /**
     * Gets the remote node identifier.
     *
     * @return
     */
    @Override
    public Node getRemoteNode() {
        return remoteNode;
    }

    /**
     * Gets the local node identifier.
     *
     * @return
     */
    @Override
    public Node getLocalNode() {
        return localNode;
    }

    /**
     * Gets the current session Id.
     *
     * @return
     */
    @Override
    public UUID getSessionId() {
        return sessionId;
    }

    /**
     * Gets the current session state.
     *
     * @return
     */
    @Override
    public Session.SessionState getState() {
        return state;
    }

    /**
     * Sets the channel listener.
     *
     * @param channelListener
     */
    @Override
    public void setChannelListener(ChannelListener channelListener) {
        this.channelListener = channelListener;
    }

    /**
     * Sends a command to the remote node.
     *
     * @param command
     */
    @Override
    public void sendCommand(Command command) throws IOException {
        if (command == null) {
            throw new IllegalArgumentException("command");
        }
        if (getState() != Session.SessionState.ESTABLISHED) {
            throw new IllegalStateException(String.format("Cannot send a command in the '%s' session state", state));
        }
        sendAsync(command);
    }

    /**
     * Sets the listener for receiving commands.
     *
     * @param commandChannelListener
     */
    @Override
    public void setCommandChannelListener(CommandChannelListener commandChannelListener) {
        this.commandChannelListener = commandChannelListener;
    }

    /**
     * Sends a message to the remote node.
     *
     * @param message
     */
    @Override
    public void sendMessage(Message message) throws IOException {
        if (message == null) {
            throw new IllegalArgumentException("message");
        }
        if (getState() != Session.SessionState.ESTABLISHED) {
            throw new IllegalStateException(String.format("Cannot send a message in the '%s' session state", state));
        }
        sendAsync(message);
    }

    /**
     * Sets the listener for receiving messages.
     *
     * @param messageChannelListener
     */
    @Override
    public void setMessageChannelListener(MessageChannelListener messageChannelListener) {
        this.messageChannelListener = messageChannelListener;
    }

    /**
     * Sends a notification to the remote node.
     *
     * @param notification
     */
    @Override
    public void sendNotification(Notification notification) throws IOException {
        if (notification == null) {
            throw new IllegalArgumentException("notification");
        }
        if (getState() != Session.SessionState.ESTABLISHED) {
            throw new IllegalStateException(String.format("Cannot send a notification in the '%s' session state", state));
        }
        sendAsync(notification);
    }

    /**
     * Sets the listener for receiving notifications.
     *
     * @param notificationChannelListener
     */
    @Override
    public void setNotificationChannelListener(NotificationChannelListener notificationChannelListener) {
        this.notificationChannelListener = notificationChannelListener;
    }

    /**
     * Sends a session to the remote node.
     *
     * @param session
     */
    @Override
    public void sendSession(Session session) throws IOException {
        if (session == null) {
            throw new IllegalArgumentException("session");
        }
        if (getState() == Session.SessionState.FINISHED || getState() == Session.SessionState.FAILED) {
            throw new IllegalStateException(String.format("Cannot send a session in the '%s' session state", state));
        }
        sendAsync(session);
    }

    /**
     * Sets the listener for receiving sessions.
     *
     * @param sessionChannelListener
     */
    @Override
    public void setSessionChannelListener(SessionChannelListener sessionChannelListener) {
        this.sessionChannelListener = sessionChannelListener;
    }
    
    
    private void sendAsync(Envelope envelope) throws IOException {
        transport.send(envelope);
    }
    
    private class EstablishedTransportListener implements Transport.TransportListener {
        /**
         * Occurs when a envelope is received by the transport.
         *
         * @param envelope
         */
        @Override
        public void onReceive(Envelope envelope) {
            if (envelope instanceof Notification) {
                NotificationChannelListener notificationChannelListener = ChannelImpl.this.notificationChannelListener;
                if (notificationChannelListener != null) {
                    notificationChannelListener.onReceiveNotification((Notification) envelope);
                }
            } else if (envelope instanceof Message) {
                MessageChannelListener messageChannelListener = ChannelImpl.this.messageChannelListener;
                if (messageChannelListener != null) {
                    messageChannelListener.onReceiveMessage((Message)envelope);
                }
            } else if (envelope instanceof Command) {
                CommandChannelListener commandChannelListener = ChannelImpl.this.commandChannelListener;
                if (commandChannelListener != null) {
                    commandChannelListener.onReceiveCommand((Command) envelope);
                }
            } else if (envelope instanceof Session) {
                SessionChannelListener sessionChannelListener = ChannelImpl.this.sessionChannelListener;
                if (sessionChannelListener != null) {
                    sessionChannelListener.onReceiveSession((Session) envelope);
                }
            }
        }

        /**
         * Occurs when the channel is about to be closed.
         */
        @Override
        public void onClosing() {
            ChannelListener channelListener = ChannelImpl.this.channelListener;
            if (channelListener != null) {
                channelListener.onTransportClosing();
            }
        }

        /**
         * Occurs after the connection was closed.
         */
        @Override
        public void onClosed() {
            ChannelListener channelListener = ChannelImpl.this.channelListener;
            if (channelListener != null) {
                channelListener.onTransportClosed();
            }
        }

        /**
         * Occurs when an exception is thrown
         * during the receive process.
         *
         * @param e The thrown exception.
         */
        @Override
        public void onException(Exception e) {
            ChannelListener channelListener = ChannelImpl.this.channelListener;
            if (channelListener != null) {
                channelListener.onTransportException(e);
            }
        }

        /**
         * Indicates if the listener is active.
         * If not, it can be removed from the registered listeners.
         *
         * @return
         */
        @Override
        public boolean isActive() {
            return ChannelImpl.this.getState() == Session.SessionState.ESTABLISHED;
        }
    }
}
