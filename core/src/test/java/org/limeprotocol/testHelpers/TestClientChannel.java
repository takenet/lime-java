package org.limeprotocol.testHelpers;

import org.limeprotocol.*;
import org.limeprotocol.client.ClientChannelImpl;
import org.limeprotocol.network.Transport;

import static org.limeprotocol.Session.SessionState;

public class TestClientChannel extends ClientChannelImpl {
    public TestClientChannel(Transport transport, SessionState state, boolean fillEnvelopeRecipients,
                             Node remoteNode, Node localNode, String sessionId,
                             boolean autoReplyPings, boolean autoNotifyReceipt) {
        super(transport, fillEnvelopeRecipients, autoReplyPings, autoNotifyReceipt);
        setRemoteNode(remoteNode);
        setLocalNode(localNode);
        setState(state);
        setSessionId(sessionId);
    }

    public void setState(SessionState state) {
        super.setState(state);
    }

    @Override
    public synchronized void raiseOnReceiveMessage(Message message) {
        super.raiseOnReceiveMessage(message);
    }
}
