package org.limeprotocol.network;

import org.junit.Test;
import org.limeprotocol.*;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.limeprotocol.testHelpers.Dummy.*;

public class ChannelBaseTest {

    private TestTransport transport;

    private ChannelBase getTarget(Session.SessionState state) {
        return getTarget(state, false);
    }

    private ChannelBase getTarget(Session.SessionState state, boolean fillEnvelopeRecipients) {
        return getTarget(state, fillEnvelopeRecipients, null, null);
    }

    private ChannelBase getTarget(Session.SessionState state, boolean fillEnvelopeRecipients, Node remoteNode, Node localNode) {
        return getTarget(state, fillEnvelopeRecipients, remoteNode, localNode, null);
    }

    private ChannelBase getTarget(Session.SessionState state, boolean fillEnvelopeRecipients, Node remoteNode, Node localNode, UUID sessionId) {
        transport = new TestTransport();
        return new TestChannel(transport, state, fillEnvelopeRecipients, remoteNode, localNode, sessionId);
    }

    @Test
    public void sendMessage_establishedState_callsTransport() throws IOException {
        // Arrange
        Message message = createMessage(createPlainDocument());
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);
        
        // Act
        target.sendMessage(message);
        
        // Assert
        assertEquals(1, transport.sentEnvelopes.size());
        assertEquals(message, transport.sentEnvelopes.remove());
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendMessage_nullMessage_throwsIllegalArgumentException() throws IOException {
        // Arrange
        Message message = null;
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);

        // Act
        target.sendMessage(message);
    }

    @Test(expected = IllegalStateException.class)
    public void sendMessage_newMessage_throwsIllegalStateException() throws IOException {
        // Arrange
        Message message = createMessage(createPlainDocument());
        ChannelBase target = getTarget(Session.SessionState.NEW);

        // Act
        target.sendMessage(message);
    }

    @Test
    public void onReceiveMessage_registeredListener_callsListener() throws InterruptedException {
        // Arrange
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);
        final Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        final List<Message> actual = new ArrayList<>();
        target.addMessageListener(new MessageChannel.MessageChannelListener() {
            
            @Override
            public void onReceiveMessage(Message message) {
                actual.add(message);
                synchronized (semaphore) {
                    semaphore.release();
                }
            }
        }, true);
        Message message = createMessage(createPlainDocument());

        // Act
        transport.raiseOnReceive(message);
        synchronized (semaphore) {
            semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS);
        }
        
        // Assert
        assertEquals(1, actual.size());
        assertEquals(message, actual.get(0));
    }

    @Test
    public void onReceiveMessage_noRecipients_fillsFromTheSession() throws InterruptedException {
        // Arrange
        Node remoteNode = createNode();
        Node localNode = createNode();
        Message message = createMessage(createPlainDocument());
        message.setFrom(null);
        message.setTo(null);
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED, true, remoteNode, localNode);
        final Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        final List<Message> actual = new ArrayList<>();
        target.addMessageListener(new MessageChannel.MessageChannelListener() {

            @Override
            public void onReceiveMessage(Message message) {
                actual.add(message);
                synchronized (semaphore) {
                    semaphore.release();
                }
            }
        }, true);

        // Act
        transport.raiseOnReceive(message);
        synchronized (semaphore) {
            semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS);
        }

        // Assert
        assertEquals(1, actual.size());
        assertEquals(message, actual.get(0));
        assertEquals(localNode, actual.get(0).getTo());
        assertEquals(remoteNode, actual.get(0).getFrom());
        assertNull(actual.get(0).getPp());
    }

    @Test
    public void onReceiveMessage_incompleteRecipients_fillsFromTheSession() throws InterruptedException {
        // Arrange
        Node remoteNode = createNode();
        Node localNode = createNode();
        Message message = createMessage(createPlainDocument());
        message.setFrom(remoteNode.copy());
        message.setTo(localNode.copy());
        message.getFrom().setDomain(null);
        message.getTo().setDomain(null);
        message.getFrom().setInstance(null);
        message.getTo().setInstance(null);
        
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED, true, remoteNode, localNode);
        final Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        final List<Message> actual = new ArrayList<>();
        target.addMessageListener(new MessageChannel.MessageChannelListener() {
            
            @Override
            public void onReceiveMessage(Message message) {
                actual.add(message);
                synchronized (semaphore) {
                    semaphore.release();
                }
            }
        }, true);

        // Act
        transport.raiseOnReceive(message);
        synchronized (semaphore) {
            semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS);
        }

        // Assert
        assertEquals(1, actual.size());
        assertEquals(message, actual.get(0));
        assertEquals(localNode.toIdentity(), actual.get(0).getTo().toIdentity());
        assertEquals(remoteNode.toIdentity(), actual.get(0).getFrom().toIdentity());
        assertNull(actual.get(0).getPp());
    }

    @Test
    public void onReceiveMessage_registeredListenerTwoReceives_callsListenerAndUnregister() throws InterruptedException {
        // Arrange
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);
        final Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        final List<Message> actual = new ArrayList<>();
        target.addMessageListener(new MessageChannel.MessageChannelListener() {
            @Override
            public void onReceiveMessage(Message message) {
                actual.add(message);
                synchronized (semaphore) {
                    semaphore.release();
                }
            }
        }, true);
        Message message = createMessage(createPlainDocument());

        // Act
        transport.raiseOnReceive(message);
        synchronized (semaphore) {
            semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS);
        }
        transport.raiseOnReceive(message);
        Thread.sleep(100);

        // Assert
        assertEquals(1, actual.size());
        assertEquals(message, actual.get(0));
    }

    @Test
    public void onReceiveMessage_registeredListenerMultipleReceives_callsListenerMultipleTimes() throws InterruptedException {
        // Arrange
        final int messageCount = createRandomInt(100) + 1;
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);
        final Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        final List<Message> actual = new ArrayList<>();
        target.addMessageListener(new MessageChannel.MessageChannelListener() {
            @Override
            public void onReceiveMessage(Message message) {
                actual.add(message);
                if (actual.size() == messageCount) {
                    synchronized (semaphore) {
                        semaphore.release();
                    }
                }
            }
        }, false);

        // Act
        for (int i = 0; i < messageCount; i++) {
            transport.raiseOnReceive(createMessage(createPlainDocument()));
        }

        synchronized (semaphore) {
            semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS);
        }

        // Assert
        assertEquals(messageCount, actual.size());
    }

    @Test
    public void sendCommand_establishedState_callsTransport() throws IOException {
        // Arrange
        Command command = createCommand(createPlainDocument());
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);

        // Act
        target.sendCommand(command);

        // Assert
        assertEquals(1, transport.sentEnvelopes.size());
        assertEquals(command, transport.sentEnvelopes.remove());
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendCommand_nullCommand_throwsIllegalArgumentException() throws IOException {
        // Arrange
        Command command = null;
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);

        // Act
        target.sendCommand(command);
    }

    @Test(expected = IllegalStateException.class)
    public void sendCommand_newCommand_throwsIllegalStateException() throws IOException {
        // Arrange
        Command command = createCommand(createPlainDocument());
        ChannelBase target = getTarget(Session.SessionState.NEW);

        // Act
        target.sendCommand(command);
    }

    @Test
    public void onReceiveCommand_registeredListener_callsListener() throws InterruptedException {
        // Arrange
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);
        final Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        final List<Command> actual = new ArrayList<>();
        target.addCommandListener(new CommandChannel.CommandChannelListener() {

            @Override
            public void onReceiveCommand(Command command) {
                actual.add(command);
                synchronized (semaphore) {
                    semaphore.release();
                }
            }
        }, true);
        Command command = createCommand(createPlainDocument());

        // Act
        transport.raiseOnReceive(command);
        synchronized (semaphore) {
            semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS);
        }

        // Assert
        assertEquals(1, actual.size());
        assertEquals(command, actual.get(0));
    }

    @Test
    public void onReceiveCommand_registeredListenerTwoReceives_callsListenerAndUnregister() throws InterruptedException {
        // Arrange
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);
        final Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        final List<Command> actual = new ArrayList<>();
        target.addCommandListener(new CommandChannel.CommandChannelListener() {
            @Override
            public void onReceiveCommand(Command command) {
                actual.add(command);
                synchronized (semaphore) {
                    semaphore.release();
                }
            }
        }, true);
        Command command = createCommand(createPlainDocument());

        // Act
        transport.raiseOnReceive(command);
        synchronized (semaphore) {
            semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS);
        }
        transport.raiseOnReceive(command);
        Thread.sleep(100);

        // Assert
        assertEquals(1, actual.size());
        assertEquals(command, actual.get(0));
    }

    @Test
    public void onReceiveCommand_registeredListenerMultipleReceives_callsListenerMultipleTimes() throws InterruptedException {
        // Arrange
        final int commandCount = createRandomInt(100) + 1;
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);
        final Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        final List<Command> actual = new ArrayList<>();
        target.addCommandListener(new CommandChannel.CommandChannelListener() {
            @Override
            public void onReceiveCommand(Command command) {
                actual.add(command);
                if (actual.size() == commandCount) {
                    synchronized (semaphore) {
                        semaphore.release();
                    }
                }
            }
        }, false);

        // Act
        for (int i = 0; i < commandCount; i++) {
            transport.raiseOnReceive(createCommand(createPlainDocument()));
        }

        synchronized (semaphore) {
            semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS);
        }

        // Assert
        assertEquals(commandCount, actual.size());
    }

    @Test
    public void sendNotification_establishedState_callsTransport() throws IOException {
        // Arrange
        Notification notification = createNotification(Notification.Event.RECEIVED);
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);

        // Act
        target.sendNotification(notification);

        // Assert
        assertEquals(1, transport.sentEnvelopes.size());
        assertEquals(notification, transport.sentEnvelopes.remove());
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendNotification_nullNotification_throwsIllegalArgumentException() throws IOException {
        // Arrange
        Notification notification = null;
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);

        // Act
        target.sendNotification(notification);
    }

    @Test(expected = IllegalStateException.class)
    public void sendNotification_newNotification_throwsIllegalStateException() throws IOException {
        // Arrange
        Notification notification = createNotification(Notification.Event.RECEIVED);
        ChannelBase target = getTarget(Session.SessionState.NEW);

        // Act
        target.sendNotification(notification);
    }

    @Test
    public void onReceiveNotification_registeredListener_callsListener() throws InterruptedException {
        // Arrange
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);
        final Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        final List<Notification> actual = new ArrayList<>();
        target.addNotificationListener(new NotificationChannel.NotificationChannelListener() {

            @Override
            public void onReceiveNotification(Notification notification) {
                actual.add(notification);
                synchronized (semaphore) {
                    semaphore.release();
                }
            }
        }, true);
        Notification notification = createNotification(Notification.Event.RECEIVED);

        // Act
        transport.raiseOnReceive(notification);
        synchronized (semaphore) {
            semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS);
        }

        // Assert
        assertEquals(1, actual.size());
        assertEquals(notification, actual.get(0));
    }

    @Test
    public void onReceiveNotification_registeredListenerTwoReceives_callsListenerAndUnregister() throws InterruptedException {
        // Arrange
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);
        final Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        final List<Notification> actual = new ArrayList<>();
        target.addNotificationListener(new NotificationChannel.NotificationChannelListener() {
            @Override
            public void onReceiveNotification(Notification notification) {
                actual.add(notification);
                synchronized (semaphore) {
                    semaphore.release();
                }
            }
        }, true);
        Notification notification = createNotification(Notification.Event.RECEIVED);

        // Act
        transport.raiseOnReceive(notification);
        synchronized (semaphore) {
            semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS);
        }
        transport.raiseOnReceive(notification);
        Thread.sleep(100);

        // Assert
        assertEquals(1, actual.size());
        assertEquals(notification, actual.get(0));
    }

    @Test
    public void onReceiveNotification_registeredListenerMultipleReceives_callsListenerMultipleTimes() throws InterruptedException {
        // Arrange
        final int notificationCount = createRandomInt(100) + 1;
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);
        final Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        final List<Notification> actual = new ArrayList<>();
        target.addNotificationListener(new NotificationChannel.NotificationChannelListener() {
            @Override
            public void onReceiveNotification(Notification notification) {
                actual.add(notification);
                if (actual.size() == notificationCount) {
                    synchronized (semaphore) {
                        semaphore.release();
                    }
                }
            }
        }, false);

        // Act
        for (int i = 0; i < notificationCount; i++) {
            transport.raiseOnReceive(createNotification(Notification.Event.RECEIVED));
        }

        synchronized (semaphore) {
            semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS);
        }

        // Assert
        assertEquals(notificationCount, actual.size());
    }

    @Test
    public void sendSession_establishedState_callsTransport() throws IOException {
        // Arrange
        Session session = createSession(Session.SessionState.ESTABLISHED);
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);

        // Act
        target.sendSession(session);

        // Assert
        assertEquals(1, transport.sentEnvelopes.size());
        assertEquals(session, transport.sentEnvelopes.remove());
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendSession_nullSession_throwsIllegalArgumentException() throws IOException {
        // Arrange
        Session session = null;
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);

        // Act
        target.sendSession(session);
    }

    @Test
    public void onReceiveSession_registeredListener_callsListener() throws InterruptedException {
        // Arrange
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);
        final Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        final List<Session> actual = new ArrayList<>();
        target.addSessionListener(new SessionChannel.SessionChannelListener() {

            @Override
            public void onReceiveSession(Session session) {
                actual.add(session);
                synchronized (semaphore) {
                    semaphore.release();
                }
            }
        });
        Session session = createSession(Session.SessionState.ESTABLISHED);

        // Act
        transport.raiseOnReceive(session);
        synchronized (semaphore) {
            semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS);
        }

        // Assert
        assertEquals(1, actual.size());
        assertEquals(session, actual.get(0));
    }

    @Test
    public void onReceiveSession_registeredListenerTwoReceives_callsListenerAndUnregister() throws InterruptedException {
        // Arrange
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);
        final Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        final List<Session> actual = new ArrayList<>();
        target.addSessionListener(new SessionChannel.SessionChannelListener() {
            @Override
            public void onReceiveSession(Session session) {
                actual.add(session);
                synchronized (semaphore) {
                    semaphore.release();
                }
            }
        });
        Session session = createSession(Session.SessionState.ESTABLISHED);

        // Act
        transport.raiseOnReceive(session);
        synchronized (semaphore) {
            semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS);
        }
        transport.raiseOnReceive(session);
        Thread.sleep(100);

        // Assert
        assertEquals(1, actual.size());
        assertEquals(session, actual.get(0));
    }


    @Test
    public void getTransport_anyInstance_returnsInstance() {
        // Arrange
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);

        // Act
        Transport actual = target.getTransport();

        // Assert
        assertEquals(transport, actual);
    }

    @Test
    public void getRemoteNode_nullInstance_returnsNull() {
        // Arrange
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);

        // Act
        Node actual = target.getRemoteNode();

        // Assert
        assertNull(actual);
    }

    @Test
    public void getRemoteNode_anyInstance_returnsInstance() {
        // Arrange
        Node remoteNode = createNode();
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED, false, remoteNode, null);

        // Act
        Node actual = target.getRemoteNode();

        // Assert
        assertEquals(remoteNode, actual);
    }

    @Test
    public void getLocalNode_nullInstance_returnsNull() {
        // Arrange
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);

        // Act
        Node actual = target.getLocalNode();

        // Assert
        assertNull(actual);
    }

    @Test
    public void getLocalNode_anyInstance_returnsInstance() {
        // Arrange
        Node localNode = createNode();
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED, false, null, localNode);

        // Act
        Node actual = target.getLocalNode();

        // Assert
        assertEquals(localNode, actual);
    }

    @Test
    public void getSessionId_nullInstance_returnsNull() {
        // Arrange
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED);

        // Act
        UUID actual = target.getSessionId();

        // Assert
        assertNull(actual);
    }

    @Test
    public void getSessionId_anyInstance_returnsInstance() {
        // Arrange
        UUID sessionId = UUID.randomUUID();
        ChannelBase target = getTarget(Session.SessionState.ESTABLISHED, false, null, null, sessionId);

        // Act
        UUID actual = target.getSessionId();

        // Assert
        assertEquals(sessionId, actual);
    }
    
    @Test
    public void getState_new_returnsInstance() {
        // Arrange
        Session.SessionState sessionState = Session.SessionState.NEW;
        ChannelBase target = getTarget(sessionState);
        
        // Act
        Session.SessionState actual = target.getState();
        
        // Assert
        assertEquals(sessionState, actual);
    }

    @Test
    public void setState_negotiation_setsValueAndStartsRemovableListener() {
        // Arrange
        Session.SessionState state = Session.SessionState.NEGOTIATING;
        ChannelBase target = getTarget(Session.SessionState.NEW);

        // Act
        ((TestChannel)target).setState(state);

        // Assert
        assertEquals(state, target.getState());
        assertEquals(1, transport.addedListeners.size());
        TransportListenerRemoveAfterReceive listener = transport.addedListeners.remove();
        assertNotNull(listener.transportListener);
        assertEquals(true, listener.removeAfterReceive);
    }

    @Test
    public void setState_established_setsValueAndStartsListener() {
        // Arrange
        Session.SessionState state = Session.SessionState.ESTABLISHED;
        ChannelBase target = getTarget(Session.SessionState.NEW);

        // Act
        ((TestChannel)target).setState(state);

        // Assert
        assertEquals(state, target.getState());
        assertEquals(1, transport.addedListeners.size());
        TransportListenerRemoveAfterReceive listener = transport.addedListeners.remove();
        assertNotNull(listener.transportListener);
        assertEquals(false, listener.removeAfterReceive);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setState_null_throwsIllegalArgumentException() {
        // Arrange
        Session.SessionState state = null;
        ChannelBase target = getTarget(Session.SessionState.NEW);

        // Act
        ((TestChannel)target).setState(state);
    }
    
    private class TestChannel extends ChannelBase {
        protected TestChannel(Transport transport, Session.SessionState state, boolean fillEnvelopeRecipients, Node remoteNode, Node localNode, UUID sessionId) {
            super(transport, fillEnvelopeRecipients);
            setRemoteNode(remoteNode);
            setLocalNode(localNode);
            setState(state);
            setSessionId(sessionId);
        }
        
        public void setState(Session.SessionState state) {
            super.setState(state);
        }
    }

    private class TestTransport extends TransportBase implements Transport {

        public URI openUri;
        public Queue<Envelope> sentEnvelopes;
        public Queue<TransportListenerRemoveAfterReceive> addedListeners;
        public boolean closeInvoked;

        public TestTransport() {
            sentEnvelopes = new LinkedBlockingQueue<>();
            addedListeners = new LinkedBlockingQueue<>();
        }

        /**
         * Closes the transport.
         */
        @Override
        protected void performClose() throws IOException {
            closeInvoked = true;
        }

        /**
         * Sends an envelope to the remote node.
         *
         * @param envelope
         */
        @Override
        public void send(Envelope envelope) throws IOException {
            sentEnvelopes.add(envelope);
        }

        /**
         * Opens the transport connection with the specified Uri.
         *
         * @param uri
         */
        @Override
        public void open(URI uri) throws IOException {
            openUri = uri;
        }

        @Override
        public void addListener(TransportListener transportListener, boolean removeAfterReceive) {
            super.addListener(transportListener, removeAfterReceive);
            addedListeners.add(new TransportListenerRemoveAfterReceive(transportListener, removeAfterReceive));
        }

        @Override
        public void removeListener(TransportListener transportListener) {
            super.removeListener(transportListener);

            TransportListenerRemoveAfterReceive transportListenerRemoveAfterReceive = null;
            
            for (TransportListenerRemoveAfterReceive addedListener : addedListeners) {
                if (addedListener.transportListener == transportListener) {
                    transportListenerRemoveAfterReceive = addedListener;
                    break;
                }
            }
            
            if (transportListenerRemoveAfterReceive != null) {
                addedListeners.remove(transportListenerRemoveAfterReceive);
            }
        }

        public void raiseOnReceive(Envelope envelope) {
            super.raiseOnReceive(envelope);
        }

        public void raiseOnException(Exception e) {
            super.raiseOnException(e);
        }
    }

    public class TransportListenerRemoveAfterReceive {
        public final Transport.TransportListener transportListener;
        public final boolean removeAfterReceive;
        public TransportListenerRemoveAfterReceive(Transport.TransportListener transportListener, boolean removeAfterReceive) {
            this.transportListener = transportListener;
            this.removeAfterReceive = removeAfterReceive;
        }
    }
}