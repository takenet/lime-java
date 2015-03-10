package org.limeprotocol.network;

import org.junit.Test;
import org.limeprotocol.*;
import org.limeprotocol.testHelpers.Dummy;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

import java.io.IOException;
import java.net.URI;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ChannelExtensionsTest {
    private TestTransport transport;
    private SessionChannel.SessionChannelListener sessionChannelListener;

    private Channel getTarget(Session.SessionState state) {
        return getTarget(state, false);
    }

    private Channel getTarget(Session.SessionState state, boolean fillEnvelopeRecipients) {
        return getTarget(state, fillEnvelopeRecipients, null, null);
    }

    private Channel getTarget(Session.SessionState state, boolean fillEnvelopeRecipients, Node remoteNode, Node localNode) {
        return getTarget(state, fillEnvelopeRecipients, remoteNode, localNode, null);
    }

    private Channel getTarget(Session.SessionState state, boolean fillEnvelopeRecipients, Node remoteNode, Node localNode, UUID sessionId) {
        return getTarget(state, fillEnvelopeRecipients, false, remoteNode, localNode, sessionId);
    }

    private Channel getTarget(Session.SessionState state, boolean fillEnvelopeRecipients, boolean autoReplyPings, Node remoteNode, Node localNode, UUID sessionId) {
        transport = new TestTransport();
        sessionChannelListener = mock(SessionChannel.SessionChannelListener.class);
        ChannelBase channelBase = new TestChannel(transport, state, fillEnvelopeRecipients, autoReplyPings, remoteNode, localNode, sessionId);
        channelBase.enqueueSessionListener(sessionChannelListener);
        return channelBase;
    }

    @Test
    public void processCommand_validCommand_sendAndReceiveResponse() throws IOException, TimeoutException, InterruptedException {
        // Arrange
        Command requestCommand = Dummy.createCommand();
        final Command responseCommand = Dummy.createCommand(Dummy.createJsonDocument());
        responseCommand.setId(requestCommand.getId());

        Channel channel = getTarget(Session.SessionState.ESTABLISHED);
        
        transport.onSentCallback = new Runnable() {
            @Override
            public void run() {
                transport.getListener().onReceive(responseCommand);
            }
        };
        
        // Act
        Command actual = ChannelExtensions.processCommand(channel, requestCommand, 5, TimeUnit.SECONDS);
        
        // Assert
        assertEquals(responseCommand, actual);
    }

    private class TestChannel extends ChannelBase {
        protected TestChannel(Transport transport, Session.SessionState state, boolean fillEnvelopeRecipients, boolean autoReplyPings, Node remoteNode, Node localNode, UUID sessionId) {
            super(transport, fillEnvelopeRecipients, autoReplyPings);
            setRemoteNode(remoteNode);
            setLocalNode(localNode);
            setState(state);
            setSessionId(sessionId);
        }

        @Override
        protected synchronized void setState(Session.SessionState state) {
            super.setState(state);
        }
    }

    private class TestTransport extends TransportBase implements Transport {
        public URI openUri;
        public Queue<Envelope> sentEnvelopes;
        public boolean closeInvoked;

        public TestTransport() {
            sentEnvelopes = new LinkedBlockingQueue<>();
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
            
            if (onSentCallback != null) {
                Thread t = new Thread(onSentCallback);
                t.start();
            }
        }

        
        public Runnable onSentCallback;
        
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
        public void setListener(TransportListener listener) {
            super.setListener(listener);
        }
    }
    
}
