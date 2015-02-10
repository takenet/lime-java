package org.limeprotocol.network;

import org.limeprotocol.*;
import org.limeprotocol.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class ChannelBase implements Channel {
    
    private final Transport transport;
    private final boolean fillEnvelopeRecipients;
    private Node remoteNode;
    private Node localNode;
    private UUID sessionId;
    private Session.SessionState state;
    private final Set<CommandChannelListener> commandListeners;
    private final Set<MessageChannelListener> messageListeners;
    private final Set<NotificationChannelListener> notificationListeners;
    private final Set<ChannelListener> channelListeners;
    private final Queue<CommandChannelListener> singleReceiveCommandListeners;
    private final Queue<NotificationChannelListener> singleReceiveNotificationListeners;
    private final Queue<MessageChannelListener> singleReceiveMessageListeners;
    private final Queue<SessionChannelListener> singleReceiveSessionListeners;
    private final Transport.TransportListener transportListener;
    
    protected ChannelBase(Transport transport, boolean fillEnvelopeRecipients) {
        this.fillEnvelopeRecipients = fillEnvelopeRecipients;
        if (transport == null) {
            throw new IllegalArgumentException("transport");
        }
        
        this.transport = transport;
        channelListeners = new HashSet<>();
        commandListeners = new HashSet<>();
        messageListeners = new HashSet<>();
        notificationListeners = new HashSet<>();
        singleReceiveCommandListeners = new LinkedBlockingQueue<>();
        singleReceiveNotificationListeners = new LinkedBlockingQueue<>();
        singleReceiveMessageListeners = new LinkedBlockingQueue<>();
        singleReceiveSessionListeners = new LinkedBlockingQueue<>();
        transportListener = new ChannelTransportListener();
        setState(Session.SessionState.NEW);
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


    protected void setRemoteNode(Node remoteNode) {
        this.remoteNode = remoteNode;
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

    public void setLocalNode(Node localNode) {
        this.localNode = localNode;
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

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
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

    protected synchronized void setState(Session.SessionState state) {
        if (state == null) {
            throw new IllegalArgumentException("state");
        }
        
        if (this.state != state) {
            this.state = state;
            setupTransportListener();
        }
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

        if (!channelListeners.contains(channelListener)) {
            channelListeners.add(channelListener);
        }
    }

    /**
     * Removes a registered channel listener.
     * * @param channelListener
     *
     * @param channelListener
     */
    @Override
    public void removeChannelListener(ChannelListener channelListener) {
        channelListeners.remove(channelListener);
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
        if (!commandListeners.remove(listener)) {
            singleReceiveCommandListeners.remove(listener);
        }
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
        if (!messageListeners.remove(listener)) {
            singleReceiveMessageListeners.remove(listener);
        }
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
        if (!notificationListeners.remove(listener)) {
            singleReceiveNotificationListeners.remove(listener);
        }
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
    public synchronized void addSessionListener(SessionChannelListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }

        singleReceiveSessionListeners.add(listener);
    }

    /**
     * Removes the specified listener.
     *
     * @param listener
     */
    @Override
    public void removeSessionListener(SessionChannelListener listener) {
        singleReceiveSessionListeners.remove(listener);
    }
    
    private void send(Envelope envelope) throws IOException {
        if (fillEnvelopeRecipients) {
            fillEnvelope(envelope, true);
        }
        
        transport.send(envelope);
    }

    private synchronized void raiseOnReceiveMessage(Message message) {
        if (getState() != Session.SessionState.ESTABLISHED) {
            throw new IllegalStateException(String.format("Cannot receive messages in the '%s' session state", state));
        }
        
        for (MessageChannelListener listener : snapshot(singleReceiveMessageListeners, messageListeners)) {
            try {
                listener.onReceiveMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void raiseOnReceiveCommand(Command command) {
        if (getState() != Session.SessionState.ESTABLISHED) {
            throw new IllegalStateException(String.format("Cannot receive commands in the '%s' session state", state));
        }
        
        for (CommandChannelListener listener : snapshot(singleReceiveCommandListeners, commandListeners)) {
            try {
                listener.onReceiveCommand(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void raiseOnReceiveNotification(Notification notification) {
        if (getState() != Session.SessionState.ESTABLISHED) {
            throw new IllegalStateException(String.format("Cannot receive notifications in the '%s' session state", state));
        }
        
        for (NotificationChannelListener listener : snapshot(singleReceiveNotificationListeners, notificationListeners)) {
            try {
                listener.onReceiveNotification(notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected synchronized void raiseOnReceiveSession(Session session) {
        for (SessionChannelListener listener : snapshot(singleReceiveSessionListeners, null)) {
            try {
                listener.onReceiveSession(session);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Merges a queue and a collection, removing all items from the queue.
     * @param queue
     * @param collection
     * @param <T>
     * @return
     */
    private static <T> List<T> snapshot(Queue<T> queue, Collection<T> collection) {
        List<T> result = new ArrayList<>();
        if (collection != null) {
            result.addAll(collection);
        }
        if (queue != null) {
            while (!queue.isEmpty()) {
                result.add(queue.remove());
            }
        }
        return result;
    }

    private synchronized void raiseOnTransportClosing() {
        for (ChannelListener listener : snapshot(null, channelListeners)) {
            try {
                listener.onTransportClosing();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void raiseOnTransportClosed() {
        for (ChannelListener listener : snapshot(null, channelListeners)) {
            try {
                listener.onTransportClosed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Remove reference with all listeners
        channelListeners.clear();
    }

    protected synchronized void raiseOnTransportException(Exception exception) {
        for (ChannelListener listener : snapshot(null, channelListeners)) {
            try {
                listener.onTransportException(exception);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected synchronized void setupTransportListener() {
        boolean removeOnReceive = true;
        if (getState() == Session.SessionState.ESTABLISHED) {
            removeOnReceive = false;
        }
        transport.removeListener(transportListener);
        transport.addListener(transportListener, removeOnReceive);
    }
    
    /**
     * Fills the envelope recipients using the session information.
     * @param envelope
     * @param isSending
     */
    protected void fillEnvelope(Envelope envelope, boolean isSending) {
        if (!isSending) {
            Node from = getRemoteNode();
            Node to = getLocalNode();

            if (from != null) {
                if (envelope.getFrom() == null) {
                    envelope.setFrom(from.copy());
                }
                else if (StringUtils.isNullOrEmpty(envelope.getFrom().getDomain())) {
                    envelope.getFrom().setDomain(from.getDomain());
                }
            }

            if (to != null) {
                if (envelope.getTo() == null) {
                    envelope.setTo(to.copy());
                }
                else if (StringUtils.isNullOrEmpty(envelope.getTo().getDomain())) {
                    envelope.getTo().setDomain(to.getDomain());
                }
            }
        }
    }

    private class ChannelTransportListener implements Transport.TransportListener {
        /**
         * Occurs when a envelope is received by the transport.
         *
         * @param envelope
         */
        @Override
        public void onReceive(Envelope envelope) {
            if (fillEnvelopeRecipients) {
                fillEnvelope(envelope, false);
            }
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
