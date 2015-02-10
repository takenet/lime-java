package org.limeprotocol.client;

import org.junit.Before;
import org.junit.Test;
import org.limeprotocol.Node;
import org.limeprotocol.Session;
import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;
import org.limeprotocol.testHelpers.TestClientChannel;
import org.limeprotocol.testHelpers.TestTransport;
import org.mockito.*;

import java.io.IOException;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static org.limeprotocol.Session.*;
import static org.limeprotocol.network.SessionChannel.*;
import static org.limeprotocol.testHelpers.Dummy.*;
import static org.mockito.Mockito.*;

public class ClientChannelImplTest {

    private TestTransport transport;
    @Mock private SessionChannelListener listener;
    @Captor private ArgumentCaptor<Session> sessionArgument;

    @Before
    public void setUp() throws Exception {
        transport = new TestTransport();
        MockitoAnnotations.initMocks(this);
    }

    //region startNewSession

    @Test
    public void startNewSession_NewState_CallsTransportAndReadsFromBuffer() throws Exception {
        // Arrange
        Session authenticationSession = createSession(SessionState.AUTHENTICATING);
        transport.addNextEnvelopeToReturn(authenticationSession);

        TestClientChannel target = getTarget();

        // Act
        target.startNewSession(listener);

        // Assert

        assertThat(transport.getSentEnvelopes()).hasSize(1);
        assertThat(transport.getSentEnvelopes()[0]).isInstanceOf(Session.class);
        Session sentSession = (Session)transport.getSentEnvelopes()[0];
        assertThat(sentSession.getState()).isEqualTo(Session.SessionState.NEW);

        verify(listener).onReceiveSession(sessionArgument.capture());
        Session sessionReturned = sessionArgument.getValue();
        assertThat(sessionReturned).isNotNull().isEqualTo(authenticationSession);
    }

    @Test
    public void startNewSession_InvalidState_ThrowsUnsupportedOperationException() throws Exception {
        // Arrange
        TestClientChannel target = getTarget(SessionState.ESTABLISHED);

        // Act
        try {
            target.startNewSession(listener);
        } catch (UnsupportedOperationException e) {
            // Assert
            assertThat(transport.getSentEnvelopes()).isEmpty();
            verify(listener, never()).onReceiveSession(any(Session.class));
            return;
        }

        fail("An UnsupportedOperationException should be threw");
    }

    //endregion startNewSession

    //region negotiateSession

    @Test
    public void negotiateSession_NegotiatingState_CallsTransportAndReadsFromBuffer() throws Exception {
        // Arrange
        TestClientChannel target = getTarget(SessionState.NEGOTIATING);

        SessionCompression compression = SessionCompression.GZIP;
        SessionEncryption encryption = SessionEncryption.TLS;

        Session negotiatingSession = createSession(SessionState.NEGOTIATING);
        negotiatingSession.setId(target.getSessionId());

        transport.addNextEnvelopeToReturn(negotiatingSession);

        ArgumentCaptor<Session> sessionArgument = ArgumentCaptor.forClass(Session.class);

        // Act
        target.negotiateSession(compression, encryption, listener);

        // Assert
        assertThat(transport.getSentEnvelopes()).hasSize(1);
        assertThat(transport.getSentEnvelopes()[0]).isInstanceOf(Session.class);
        Session sentSession = (Session)transport.getSentEnvelopes()[0];
        assertThat(sentSession.getState()).isEqualTo(SessionState.NEGOTIATING);
        assertThat(sentSession.getCompression()).isEqualTo(compression);
        assertThat(sentSession.getEncryption()).isEqualTo(encryption);

        verify(listener).onReceiveSession(sessionArgument.capture());
        Session sessionReturned = sessionArgument.getValue();
        assertThat(sessionReturned).isNotNull().isEqualTo(negotiatingSession);
    }

    @Test
    public void negotiateSession_InvalidState_ThrowsUnsupportedOperationException() throws Exception {
        // Arrange
        TestClientChannel target = getTarget(SessionState.NEW);

        SessionCompression compression = SessionCompression.GZIP;
        SessionEncryption encryption = SessionEncryption.TLS;

        // Act
        try {
            target.negotiateSession(compression, encryption, listener);
        } catch (UnsupportedOperationException e) {
            // Assert
            assertThat(transport.getSentEnvelopes()).isEmpty();
            verify(listener, never()).onReceiveSession(any(Session.class));
            return;
        }

        fail("An UnsupportedOperationException should be threw");
    }
    //endregion negotiateSession

    private TestClientChannel getTarget() {
        return getTarget(SessionState.NEW);
    }

    private TestClientChannel getTarget(Session.SessionState state) {
        return getTarget(state, false, null, null, UUID.randomUUID());
    }

    private TestClientChannel getTarget(Session.SessionState state, boolean fillEnvelopeRecipients, Node remoteNode, Node localNode, UUID sessionId) {
        return new TestClientChannel(transport, state, fillEnvelopeRecipients, remoteNode, localNode, sessionId);
    }
}