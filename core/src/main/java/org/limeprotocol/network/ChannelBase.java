package org.limeprotocol.network;

import org.limeprotocol.*;
import org.limeprotocol.network.modules.FillEnvelopeRecipientsChannelModule;
import org.limeprotocol.network.modules.RemotePingChannelModule;
import org.limeprotocol.network.modules.ReplyPingChannelModule;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static org.limeprotocol.Session.SessionState.*;

public abstract class ChannelBase implements Channel {


    private final Transport transport;

    private Node remoteNode;
    private Node localNode;
    private String sessionId;
    private Session.SessionState state;

    private final Collection<ChannelModule<Message>> messageModules;
    private final Collection<ChannelModule<Notification>> notificationModules;
    private final Collection<ChannelModule<Command>> commandModules;

    private final Set<CommandChannelListener> commandListeners;
    private final Set<MessageChannelListener> messageListeners;
    private final Set<NotificationChannelListener> notificationListeners;
    private final Queue<CommandChannelListener> singleReceiveCommandListeners;
    private final Queue<NotificationChannelListener> singleReceiveNotificationListeners;
    private final Queue<MessageChannelListener> singleReceiveMessageListeners;
    private final Queue<SessionChannelListener> sessionChannelListeners;
    private final Transport.TransportEnvelopeListener transportEnvelopeListener;

    protected ChannelBase(Transport transport, boolean fillEnvelopeRecipients, boolean autoReplyPings, long pingInterval, long pingDisconnectionInterval) {
        if (transport == null) {
            throw new IllegalArgumentException("The argument transport cannot be null");
        }
        this.transport = transport;

        messageModules = new ArrayList<>();
        notificationModules = new ArrayList<>();
        commandModules = new ArrayList<>();
        commandListeners = new HashSet<>();
        messageListeners = new HashSet<>();
        notificationListeners = new HashSet<>();
        singleReceiveCommandListeners = new LinkedBlockingQueue<>();
        singleReceiveNotificationListeners = new LinkedBlockingQueue<>();
        singleReceiveMessageListeners = new LinkedBlockingQueue<>();
        sessionChannelListeners = new LinkedBlockingQueue<>();
        transportEnvelopeListener = new ChannelTransportEnvelopeListener();

        setState(NEW);

        if (fillEnvelopeRecipients) {
            FillEnvelopeRecipientsChannelModule.createAndRegister(this);
        }

        if (autoReplyPings) {
            commandModules.add(new ReplyPingChannelModule(this));
        }

        if (pingInterval > 0) {
            RemotePingChannelModule.createAndRegister(this, pingInterval, pingDisconnectionInterval);
        }
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
    public String getSessionId() {
        return sessionId;
    }

    protected void setSessionId(String sessionId) {
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

        onStateChanged(messageModules, state);
        onStateChanged(notificationModules, state);
        onStateChanged(commandModules, state);
    }

    @Override
    public Collection<ChannelModule<Message>> getMessageModules() {
        return messageModules;
    }

    @Override
    public Collection<ChannelModule<Notification>> getNotificationModules() {
        return notificationModules;
    }

    @Override
    public Collection<ChannelModule<Command>> getCommandModules() {
        return commandModules;
    }

    /**
     * Sends a command to the remote node.
     *
     * @param command
     */
    @Override
    public void sendCommand(Command command) throws IOException {
        send(command, commandModules);
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
     * Processes a command request, awaiting for the response.
     *
     * @param requestCommand
     * @return
     */
    @Override
    public Command processCommand(Command requestCommand, long timeout, TimeUnit timeUnit) throws IOException, TimeoutException {
        // TODO: implement
        return null;
    }

    /**
     * Sends a message to the remote node.
     *
     * @param message
     */
    @Override
    public void sendMessage(Message message) throws IOException {
        send(message, messageModules);
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
        send(notification, notificationModules);
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

        message = invokeModulesOnReceiving(message, messageModules);
        if (message != null) {
            for (MessageChannelListener listener : snapshot(singleReceiveMessageListeners, messageListeners)) {
                try {
                    listener.onReceiveMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected synchronized void raiseOnReceiveCommand(Command command) {
        ensureSessionEstablished();

        command = invokeModulesOnReceiving(command, commandModules);
        if (command != null) {
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

        notification = invokeModulesOnReceiving(notification, notificationModules);
        if (notification != null) {
            for (NotificationChannelListener listener : snapshot(singleReceiveNotificationListeners, notificationListeners)) {
                try {
                    listener.onReceiveNotification(notification);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private <T extends Envelope> T invokeModulesOnReceiving(T envelope, Collection<ChannelModule<T>> modules) {
        for (ChannelModule<T> module : new ArrayList<>(modules)) {
            if (envelope == null) break;
            envelope = module.onReceiving(envelope);
        }

        return envelope;
    }

    protected synchronized void raiseOnReceiveSession(Session session) {
        if (getState() != ESTABLISHED) {
            // Remove the envelope listener to signal the transport
            // that we are not expecting another envelope for now.
            transport.setEnvelopeListener(null);
        }

        // Remove the first listener of the queue
        SessionChannelListener listener = sessionChannelListeners.poll();
        if (listener != null) {
            listener.onReceiveSession(session);
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

    private <TListener> void addListener(TListener listener, boolean removeAfterReceive, Set<TListener> listeners, Queue<TListener> singleReceiveListeners) {
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

    private <TListener> void removeListener(TListener listener, Set<TListener> listeners, Queue<TListener> singleReceiveListeners) {
        if (!listeners.remove(listener)) {
            singleReceiveListeners.remove(listener);
        }
    }

    private <T extends Envelope> void send(T envelope, Collection<ChannelModule<T>> modules) throws IOException {
        if (envelope == null) {
            throw new IllegalArgumentException("envelope");
        }
        if (getState() != ESTABLISHED) {
            throw new IllegalStateException(String.format("Cannot send in the '%s' session state", state));
        }

        for (ChannelModule<T> module : new ArrayList<>(modules)) {
            if (envelope == null) break;
            envelope = module.onSending(envelope);
        }

        if (envelope != null) {
            send(envelope);
        }
    }

    private void send(Envelope envelope) throws IOException {
        if (!transport.isConnected()) {
            throw new IllegalStateException("The transport is not connected");
        }

        transport.send(envelope);
    }

    /**
     * Merges a queue and a collection, removing all items from the queue.
     * @param queue
     * @param collection
     * @param <T>
     * @return
     */
    private synchronized static <T> Iterable<T> snapshot(Queue<T> queue, Collection<T> collection) {
        List<T> result = new ArrayList<>();
        if (collection != null) {
            Iterator<T> iterator = collection.iterator();
            while (iterator.hasNext()) {
                result.add(iterator.next());
            }
        }
        if (queue != null) {
            while (!queue.isEmpty()) {
                result.add(queue.remove());
            }
        }
        return result;
    }

    private static <T extends Envelope> void onStateChanged(Collection<ChannelModule<T>> modules, Session.SessionState state) {
        for (ChannelModule<T> module: new ArrayList<>(modules)) {
            module.onStateChanged(state);
        }
    }

    private class ChannelTransportEnvelopeListener implements Transport.TransportEnvelopeListener {

        /**
         * Occurs when a envelope is received by the transport.
         *
         * @param envelope
         */
        @Override
        public void onReceive(Envelope envelope) {
            if (envelope instanceof Notification) {
                raiseOnReceiveNotification((Notification) envelope);
            } else if (envelope instanceof Message) {
                raiseOnReceiveMessage((Message) envelope);
            } else if (envelope instanceof Command) {
                raiseOnReceiveCommand((Command) envelope);
            } else if (envelope instanceof Session) {
                raiseOnReceiveSession((Session) envelope);
            }
        }
    }
}
