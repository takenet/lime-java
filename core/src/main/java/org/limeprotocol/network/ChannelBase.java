package org.limeprotocol.network;

import org.limeprotocol.*;
import org.limeprotocol.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static org.limeprotocol.Session.SessionState.*;

public abstract class ChannelBase implements Channel {
    
    private final static String PING_URI_TEMPLATE = "/ping";
    private final static MediaType PING_MEDIA_TYPE = MediaType.parse("application/vnd.lime.ping+json");
    
    private final Transport transport;
    private final boolean fillEnvelopeRecipients;
    private final boolean autoReplyPings;
    private Node remoteNode;
    private Node localNode;
    private UUID sessionId;
    private Session.SessionState state;
    private final Set<CommandChannelListener> commandListeners;
    private final Set<MessageChannelListener> messageListeners;
    private final Set<NotificationChannelListener> notificationListeners;
    private final Queue<CommandChannelListener> singleReceiveCommandListeners;
    private final Queue<NotificationChannelListener> singleReceiveNotificationListeners;
    private final Queue<MessageChannelListener> singleReceiveMessageListeners;
    private Queue<SessionChannelListener> sessionChannelListeners;
    private final Transport.TransportEnvelopeListener transportEnvelopeListener;

    protected ChannelBase(Transport transport, boolean fillEnvelopeRecipients, boolean autoReplyPings) {
        if (transport == null) {
            throw new IllegalArgumentException("transport");
        }
        this.transport = transport;
        this.fillEnvelopeRecipients = fillEnvelopeRecipients;
        this.autoReplyPings = autoReplyPings;
        
        commandListeners = new HashSet<>();
        messageListeners = new HashSet<>();
        notificationListeners = new HashSet<>();
        singleReceiveCommandListeners = new LinkedBlockingQueue<>();
        singleReceiveNotificationListeners = new LinkedBlockingQueue<>();
        singleReceiveMessageListeners = new LinkedBlockingQueue<>();
        sessionChannelListeners = new LinkedBlockingQueue<>();

        transportEnvelopeListener = new ChannelTransportEnvelopeListener();

        setState(NEW);
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

    protected void setLocalNode(Node localNode) {
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

    protected void setSessionId(UUID sessionId) {
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
        this.state = state;
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
        if (getState() != ESTABLISHED) {
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
        addListener(listener, removeAfterReceive, commandListeners, singleReceiveCommandListeners);
    }

    /**
     * Removes the specified listener.
     *
     * @param listener
     */
    @Override
    public void removeCommandListener(CommandChannelListener listener) {
        removeListener(listener, commandListeners, singleReceiveCommandListeners);
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
        if (getState() != ESTABLISHED) {
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
        addListener(listener, removeAfterReceive, messageListeners, singleReceiveMessageListeners);
    }

    /**
     * Removes the specified listener.
     *
     * @param listener
     */
    @Override
    public void removeMessageListener(MessageChannelListener listener) {
        removeListener(listener, messageListeners, singleReceiveMessageListeners);
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
        if (getState() != ESTABLISHED) {
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
        addListener(listener, removeAfterReceive, notificationListeners, singleReceiveNotificationListeners);
    }

    /**
     * Removes the specified listener.
     *
     * @param listener
     */
    @Override
    public void removeNotificationListener(NotificationChannelListener listener) {
        removeListener(listener, notificationListeners, singleReceiveNotificationListeners);
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
        if (getState() == FINISHED || getState() == FAILED) {
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
    public synchronized void enqueueSessionListener(SessionChannelListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }
        sessionChannelListeners.add(listener);
        setupTransportListener();
    }

    protected synchronized void raiseOnReceiveMessage(Message message) {
        ensureSessionEstablished();

        for (MessageChannelListener listener : snapshot(singleReceiveMessageListeners, messageListeners)) {
            try {
                listener.onReceiveMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected synchronized void raiseOnReceiveCommand(Command command) {
        ensureSessionEstablished();
        if (autoReplyPings &&
                command.getId() != null &&
                command.getMethod() == Command.CommandMethod.GET &&
                command.getStatus() == null &&
                command.getUri() != null &&
                command.getUri().toString().equalsIgnoreCase(PING_URI_TEMPLATE)) {
            Command pingCommandResponse = new Command(command.getId());
            pingCommandResponse.setTo(command.getFrom());
            pingCommandResponse.setMethod(Command.CommandMethod.GET);
            pingCommandResponse.setStatus(Command.CommandStatus.SUCCESS);
            pingCommandResponse.setResource(new JsonDocument(PING_MEDIA_TYPE));
            try {
                sendCommand(pingCommandResponse);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not send a ping response to the remote node", e);
            }
        }
        else {
            for (CommandChannelListener listener : snapshot(singleReceiveCommandListeners, commandListeners)) {
                try {
                    listener.onReceiveCommand(command);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected synchronized void raiseOnReceiveNotification(Notification notification) {
        ensureSessionEstablished();

        for (NotificationChannelListener listener : snapshot(singleReceiveNotificationListeners, notificationListeners)) {
            try {
                listener.onReceiveNotification(notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected synchronized void raiseOnReceiveSession(Session session) {
        if (getState() != ESTABLISHED) {
            transport.setEnvelopeListener(null);
        }

        SessionChannelListener listener = sessionChannelListeners.poll();
        if (listener != null) {
            listener.onReceiveSession(session);
        }
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

    private void ensureSessionEstablished() {
        if (getState() != ESTABLISHED) {
            throw new IllegalStateException(String.format("Cannot receive in the '%s' session state", state));
        }
    }
    
    private synchronized void setupTransportListener() {
        transport.setEnvelopeListener(transportEnvelopeListener);
    }

    private synchronized <TListener> void addListener(TListener listener, boolean removeAfterReceive, Set<TListener> listeners, Queue<TListener> singleReceiveListeners) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }

        if (!singleReceiveListeners.contains(listener) &&
                !listeners.contains(listener)) {
            if (removeAfterReceive) {
                singleReceiveListeners.add(listener);
            } else {
                listeners.add(listener);
            }
        }
    }

    private synchronized <TListener> void removeListener(TListener listener, Set<TListener> listeners, Queue<TListener> singleReceiveListeners) {
        if (!listeners.remove(listener)) {
            singleReceiveListeners.remove(listener);
        }
    }

    /**
     * Merges a queue and a collection, removing all items from the queue.
     * @param queue
     * @param collection
     * @param <T>
     * @return
     */
    private synchronized <T> Iterable<T> snapshot(Queue<T> queue, Collection<T> collection) {
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

    private void send(Envelope envelope) throws IOException {
        if (fillEnvelopeRecipients) {
            fillEnvelope(envelope, true);
        }

        transport.send(envelope);
    }

    private class ChannelTransportEnvelopeListener implements Transport.TransportEnvelopeListener {

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
    }
}
