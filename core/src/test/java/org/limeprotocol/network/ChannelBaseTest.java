package org.limeprotocol.network;

import org.junit.Test;
import org.limeprotocol.Node;
import org.limeprotocol.Session;
import org.limeprotocol.testHelpers.TestDummy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ChannelBaseTest {

    private Transport transport;

    private ChannelBase getTarget(Session.SessionState state) {
        return getTarget(state, false);
    }

    private ChannelBase getTarget(Session.SessionState state, boolean fillEnvelopeRecipients) {
        return getTarget(state, fillEnvelopeRecipients, null, null);
    }
    
    private ChannelBase getTarget(Session.SessionState state, boolean fillEnvelopeRecipients, Node remoteNode, Node localNode) {
        transport = mock(Transport.class);
        return new TestChannel(transport, state, fillEnvelopeRecipients, remoteNode, localNode);
    }
    
    @Test
    public void sendMessage_establishedState_callsTransport() {
        // Arrange
        ChannelBase channelBase = getTarget(Session.SessionState.ESTABLISHED);


        
        // Act
        
        // Assert
        
    }
    
    
    
    private class TestChannel extends ChannelBase {
        protected TestChannel(Transport transport, Session.SessionState state, boolean fillEnvelopeRecipients, Node remoteNode, Node localNode) {
            super(transport, fillEnvelopeRecipients);
            setRemoteNode(remoteNode);
            setLocalNode(localNode);
            setState(state);
        }
    }
}