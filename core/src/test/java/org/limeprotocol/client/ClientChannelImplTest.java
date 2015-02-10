package org.limeprotocol.client;

import org.junit.Before;
import org.junit.Test;
import org.limeprotocol.*;
import org.limeprotocol.security.Authentication;
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

    //region authenticateSession

    @Test
    public void authenticateSessionAsync_AuthenticatingState_CallsTransportAndReadsFromTransport() throws Exception {
        // Arrange
        TestClientChannel target = getTarget(SessionState.AUTHENTICATING);

        Identity localIdentity = createIdentity();
        String localInstance = createInstanceName();
        Authentication plainAuthentication = createPlainAuthentication();

        Session establishedSession = createSession(SessionState.ESTABLISHED);
        establishedSession.setId(target.getSessionId());

        transport.addNextEnvelopeToReturn(establishedSession);

        ArgumentCaptor<Session> sessionArgument = ArgumentCaptor.forClass(Session.class);

        // Act
        target.authenticateSession(localIdentity, plainAuthentication, localInstance, listener);

        // Assert
        assertThat(transport.getSentEnvelopes()).hasSize(1);
        assertThat(transport.getSentEnvelopes()[0]).isInstanceOf(Session.class);
        Session sentSession = (Session)transport.getSentEnvelopes()[0];
        assertThat(sentSession.getState()).isEqualTo(SessionState.AUTHENTICATING);
        assertThat(sentSession.getFrom().toIdentity()).isEqualTo(localIdentity);
        assertThat(sentSession.getFrom().getInstance()).isEqualTo(localInstance);
        assertThat(sentSession.getAuthentication()).isEqualTo(plainAuthentication);

        verify(listener).onReceiveSession(sessionArgument.capture());
        Session sessionReturned = sessionArgument.getValue();
        assertThat(sessionReturned).isNotNull().isEqualTo(establishedSession);
    }

    @Test
    public void authenticateSessionAsync_InvalidState_ThrowsUnsupportedOperationException() throws Exception {
        // Arrange
        TestClientChannel target = getTarget(SessionState.ESTABLISHED);

        Identity localIdentity = createIdentity();
        String localInstance = createInstanceName();
        Authentication plainAuthentication = createPlainAuthentication();

        // Act
        try {
            target.authenticateSession(localIdentity, plainAuthentication, localInstance, listener);
        } catch (UnsupportedOperationException e) {
            // Assert
            assertThat(transport.getSentEnvelopes()).isEmpty();
            verify(listener, never()).onReceiveSession(any(Session.class));
            return;
        }

        fail("An UnsupportedOperationException should be threw");
    }

    @Test
    public void authenticateSessionAsync_NullIdentity_ThrowsIllegalArgumentException() throws Exception {
        // Arrange
        TestClientChannel target = getTarget(SessionState.AUTHENTICATING);

        Identity localIdentity = null;
        String localInstance = createInstanceName();
        Authentication plainAuthentication = createPlainAuthentication();

        // Act
        try {
            target.authenticateSession(localIdentity, plainAuthentication, localInstance, listener);
        } catch (IllegalArgumentException e) {
            // Assert
            assertThat(transport.getSentEnvelopes()).isEmpty();
            verify(listener, never()).onReceiveSession(any(Session.class));
            return;
        }

        fail("An UnsupportedOperationException should be threw");
    }

    @Test
    public void authenticateSessionAsync_NullAuthentication_ThrowsIllegalArgumentException() throws Exception {
        // Arrange
        TestClientChannel target = getTarget(SessionState.AUTHENTICATING);

        Identity localIdentity = createIdentity();
        String localInstance = createInstanceName();
        Authentication plainAuthentication = null;

        // Act
        try {
            target.authenticateSession(localIdentity, plainAuthentication, localInstance, listener);
        } catch (IllegalArgumentException e) {
            // Assert
            assertThat(transport.getSentEnvelopes()).isEmpty();
            verify(listener, never()).onReceiveSession(any(Session.class));
            return;
        }

        fail("An UnsupportedOperationException should be threw");
    }

    //endregion authenticateSession

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