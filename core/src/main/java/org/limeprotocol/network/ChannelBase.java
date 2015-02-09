package org.limeprotocol.network;

import org.limeprotocol.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class ChannelBase implements Channel {
    
    private final Transport transport;
    private Node remoteNode;
    private Node localNode;
    private UUID sessionId;
    private Session.SessionState state;
    private final Set<CommandChannelListener> commandListeners;
    private final Set<MessageChannelListener> messageListeners;
    private final Set<NotificationChannelListener> notificationListeners;
    private final Set<SessionChannelListener> sessionListeners;
    private final Set<ChannelListener> channelListeners;
    private final Queue<CommandChannelListener> singleReceiveCommandListeners;
    private final Queue<NotificationChannelListener> singleReceiveNotificationListeners;
    private final Queue<MessageChannelListener> singleReceiveMessageListeners;
    private final Queue<SessionChannelListener> singleReceiveSessionListeners;
    private final Queue<ChannelListener> singleExceptionChannelListeners;
    
    protected ChannelBase(Transport transport) {
        if (transport == null) {
            throw new IllegalArgumentException("transport");
        }
        
        this.transport = transport;
        state = Session.SessionState.NEW;
        channelListeners = new HashSet<>();
        commandListeners = new HashSet<>();
        messageListeners = new HashSet<>();
        notificationListeners = new HashSet<>();
        sessionListeners = new HashSet<>();
        singleReceiveCommandListeners = new LinkedBlockingQueue<>();
        singleReceiveNotificationListeners = new LinkedBlockingQueue<>();
        singleReceiveMessageListeners = new LinkedBlockingQueue<>();
        singleReceiveSessionListeners = new LinkedBlockingQueue<>();
        singleExceptionChannelListeners = new LinkedBlockingQueue<>();
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

    
    protected void setState(Session.SessionState state) {
        this.state = state;
        if (state == Session.SessionState.ESTABLISHED) {
            transport.addListener(new ChannelTransportListener(), false);
        }
    }
    
    /**
     * Sets the channel listener.
     *
     * @param channelListener
     */
    @Override
    public void addChannelListener(ChannelListener channelListener, boolean removeOnException) {
        if (channelListener == null) {
            throw new IllegalArgumentException("channelListener");
        }
        if (removeOnException) {
            singleExceptionChannelListeners.add(channelListener);
        } else {
            channelListeners.add(channelListener);
        }

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
     * @param removeAfterReceive
     */
    @Override
    public void addCommandListener(CommandChannelListener listener, boolean removeAfterReceive) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }
        if (removeAfterReceive) {
            singleReceiveCommandListeners.add(listener);
        } else {
            commandListeners.add(listener);
        }
    }

    /**
     * Removes the specified listener.
     *
     * @param listener
     */
    @Override
    public void removeCommandListener(CommandChannelListener listener) {
        commandListeners.remove(listener);
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
     * @param removeAfterReceive
     */
    @Override
    public void addMessageListener(MessageChannelListener listener, boolean removeAfterReceive) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }
        if (removeAfterReceive) {
            singleReceiveMessageListeners.add(listener);
        } else {
            messageListeners.add(listener);
        }
    }

    /**
     * Removes the specified listener.
     *
     * @param listener
     */
    @Override
    public void removeMessageListener(MessageChannelListener listener) {
        messageListeners.remove(listener);
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
     * @param removeAfterReceive
     */
    @Override
    public void addNotificationListener(NotificationChannelListener listener, boolean removeAfterReceive) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }
        if (removeAfterReceive) {
            singleReceiveNotificationListeners.add(listener);
        } else {
            notificationListeners.add(listener);
        }
    }

    /**
     * Removes the specified listener.
     *
     * @param listener
     */
    @Override
    public void removeNotificationListener(NotificationChannelListener listener) {
        notificationListeners.remove(listener);
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
     * @param removeAfterReceive
     */
    @Override
    public void addSessionListener(SessionChannelListener listener, boolean removeAfterReceive) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }
        if (removeAfterReceive) {
            singleReceiveSessionListeners.add(listener);
        } else {
            sessionListeners.add(listener);
        }
    }

    /**
     * Removes the specified listener.
     *
     * @param listener
     */
    @Override
    public void removeSessionListener(SessionChannelListener listener) {
        sessionListeners.remove(listener);
    }

    private void send(Envelope envelope) throws IOException {
        transport.send(envelope);
    }
    
    private synchronized void raiseOnReceiveMessage(Message message) {
        for (MessageChannelListener listener : messageListeners) {
            try {
                listener.onReceiveMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        while (!singleReceiveMessageListeners.isEmpty()) {
            try {
                MessageChannelListener listener = singleReceiveMessageListeners.remove();
                listener.onReceiveMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void raiseOnReceiveCommand(Command command) {
        for (CommandChannelListener listener : commandListeners) {
            try {
                listener.onReceiveCommand(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        while (!singleReceiveCommandListeners.isEmpty()) {
            try {
                CommandChannelListener listener = singleReceiveCommandListeners.remove();
                listener.onReceiveCommand(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void raiseOnReceiveNotification(Notification notification) {
        for (NotificationChannelListener listener : notificationListeners) {
            try {
                listener.onReceiveNotification(notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        while (!singleReceiveNotificationListeners.isEmpty()) {
            try {
                NotificationChannelListener listener = singleReceiveNotificationListeners.remove();
                listener.onReceiveNotification(notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void raiseOnReceiveSession(Session session) {
        for (SessionChannelListener listener : sessionListeners) {
            try {
                listener.onReceiveSession(session);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        while (!singleReceiveSessionListeners.isEmpty()) {
            SessionChannelListener listener = singleReceiveSessionListeners.remove();
            try {
                listener.onReceiveSession(session);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void raiseOnTransportClosing() {
        for (ChannelListener listener : channelListeners) {
            try {
                listener.onTransportClosing();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void raiseOnTransportClosed() {
        for (ChannelListener listener : channelListeners) {
            try {
                listener.onTransportClosed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Remove reference with all listeners
        channelListeners.clear();
    }

    private synchronized void raiseOnTransportException(Exception exception) {
        for (ChannelListener listener : channelListeners) {
            try {
                listener.onTransportException(exception);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        while (!singleExceptionChannelListeners.isEmpty()) {
            ChannelListener listener = singleExceptionChannelListeners.remove();
            try {
                listener.onTransportException(exception);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected class ChannelTransportListener implements Transport.TransportListener {
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
    }
}
