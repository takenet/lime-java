package org.limeprotocol.network;

import org.limeprotocol.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChannelBase implements Channel {
    
    private final Transport transport;
    private Node remoteNode;
    private Node localNode;
    private UUID sessionId;
    private Session.SessionState state;
    private final Set<CommandChannelListener> commandChannelListeners;
    private final Set<MessageChannelListener> messageChannelListeners;
    private final Set<NotificationChannelListener> notificationChannelListeners;
    private final Set<SessionChannelListener> sessionChannelListeners;
    private final Set<ChannelListener> channelListeners;

    protected ChannelBase(Transport transport) {
        if (transport == null) {
            throw new IllegalArgumentException("transport");
        }
        
        this.transport = transport;
        this.state = Session.SessionState.NEW;
        channelListeners = new HashSet<>();
        commandChannelListeners = new HashSet<>();
        messageChannelListeners = new HashSet<>();
        notificationChannelListeners = new HashSet<>();
        sessionChannelListeners = new HashSet<>();
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
    public void addChannelListener(ChannelListener channelListener) {
        if (channelListener == null) {
            throw new IllegalArgumentException("channelListener");
        }
        channelListeners.add(channelListener);
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
        send(command);
    }

    /**
     * Sets the listener for receiving commands.
     *
     * @param listener
     */
    @Override
    public void addCommandListener(CommandChannelListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }
        commandChannelListeners.add(listener);
    }

    /**
     * Removes the specified listener.
     *
     * @param listener
     */
    @Override
    public void removeCommandListener(CommandChannelListener listener) {
        commandChannelListeners.remove(listener);
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
        send(message);
    }

    /**
     * Sets the listener for receiving messages.
     *
     * @param listener
     */
    @Override
    public void addMessageListener(MessageChannelListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }
        messageChannelListeners.add(listener);
    }

    /**
     * Removes the specified listener.
     *
     * @param listener
     */
    @Override
    public void removeMessageListener(MessageChannelListener listener) {
        messageChannelListeners.remove(listener);
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
        send(notification);
    }

    /**
     * Sets the listener for receiving notifications.
     *
     * @param listener
     */
    @Override
    public void addNotificationListener(NotificationChannelListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }
        notificationChannelListeners.add(listener);
    }

    /**
     * Removes the specified listener.
     *
     * @param listener
     */
    @Override
    public void removeNotificationListener(NotificationChannelListener listener) {
        notificationChannelListeners.remove(listener);
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
        send(session);
    }

    /**
     * Sets the listener for receiving sessions.
     *
     * @param listener
     */
    @Override
    public void addSessionListener(SessionChannelListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }
        sessionChannelListeners.add(listener);
    }

    /**
     * Removes the specified listener.
     *
     * @param listener
     */
    @Override
    public void removeSessionListener(SessionChannelListener listener) {
        sessionChannelListeners.remove(listener);
    }

    private void send(Envelope envelope) throws IOException {
        transport.send(envelope);
    }
    
    private synchronized void raiseOnReceiveMessage(Message message) {
        for (MessageChannelListener messageChannelListener : messageChannelListeners) {
            try {
                messageChannelListener.onReceiveMessage(message);    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void raiseOnReceiveCommand(Command command) {
        for (CommandChannelListener commandChannelListener : commandChannelListeners) {
            try {
                commandChannelListener.onReceiveCommand(command);    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void raiseOnReceiveNotification(Notification notification) {
        for (NotificationChannelListener notificationChannelListener : notificationChannelListeners) {
            try {
                notificationChannelListener.onReceiveNotification(notification);    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void raiseOnReceiveSession(Session session) {
        for (SessionChannelListener sessionChannelListener : sessionChannelListeners) {
            try {
                sessionChannelListener.onReceiveSession(session);    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void raiseOnTransportClosing() {
        for (ChannelListener channelListener : channelListeners) {
            try {
                channelListener.onTransportClosing();    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void raiseOnTransportClosed() {
        for (ChannelListener channelListener : channelListeners) {
            try {
                channelListener.onTransportClosed();    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void raiseOnTransportException(Exception exception) {
        for (ChannelListener channelListener : channelListeners) {
            try {
                channelListener.onTransportException(exception);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                raiseOnReceiveNotification((Notification)envelope);
            } else if (envelope instanceof Message) {
                raiseOnReceiveMessage((Message)envelope);
            } else if (envelope instanceof Command) {
                raiseOnReceiveCommand((Command) envelope);
            } else if (envelope instanceof Session) {
                raiseOnReceiveSession((Session) envelope);
            }
        }

        /**
         * Occurs when the channel is about to be closed.
         */
        @Override
        public void onClosing() {
            raiseOnTransportClosing();
        }

        /**
         * Occurs after the connection was closed.
         */
        @Override
        public void onClosed() {
            raiseOnTransportClosed();
        }

        /**
         * Occurs when an exception is thrown
         * during the receive process.
         *
         * @param e The thrown exception.
         */
        @Override
        public void onException(Exception e) {
            raiseOnTransportException(e);
        }

        /**
         * Indicates if the listener is active.
         * If not, it can be removed from the registered listeners.
         *
         * @return
         */
        @Override
        public boolean isActive() {
            return getState() == Session.SessionState.ESTABLISHED;
        }
    }
}
