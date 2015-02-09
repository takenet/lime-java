package org.limeprotocol.client;

import org.junit.Before;
import org.junit.Test;
import org.limeprotocol.Session;
import org.limeprotocol.testHelpers.TestTransport;
import org.mockito.ArgumentCaptor;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.limeprotocol.network.SessionChannel.*;
import static org.limeprotocol.testHelpers.Dummy.*;
import static org.mockito.Mockito.*;

public class ClientChannelImplTest {

    private ClientChannel target;
    private TestTransport transport;

    @Before
    public void setUp() throws Exception {
        transport = new TestTransport();
        target = new ClientChannelImpl(transport, false);
    }

    @Test
    public void StartNewSessionAsync_NewState_CallsTransportAndReadsFromBuffer() throws Exception {
        // Arrange
        Session authenticationSession = createSession(Session.SessionState.AUTHENTICATING);
        transport.addNextEnvelopeToReturn(authenticationSession);

        ArgumentCaptor<Session> sessionArgument = ArgumentCaptor.forClass(Session.class);
        SessionChannelListener listener = mock(SessionChannelListener.class);

        // Act
        target.startNewSession(listener);

        // Assert
        verify(listener).onReceiveSession(sessionArgument.capture());
        Session sessionReturned = sessionArgument.getValue();
        assertThat(sessionReturned).isNotNull().isEqualTo(authenticationSession);
    }

}