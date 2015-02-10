package org.limeprotocol.testHelpers;

import org.limeprotocol.Node;
import org.limeprotocol.client.ClientChannelImpl;
import org.limeprotocol.network.Transport;

import java.util.UUID;

import static org.limeprotocol.Session.SessionState;

public class TestClientChannel extends ClientChannelImpl {
    public TestClientChannel(Transport transport, SessionState state, boolean fillEnvelopeRecipients, Node remoteNode, Node localNode, UUID sessionId) {
        super(transport, fillEnvelopeRecipients);
        setRemoteNode(remoteNode);
        setLocalNode(localNode);
        setState(state);
        setSessionId(sessionId);
    }

    public void setState(SessionState state) {
        super.setState(state);
    }

}
