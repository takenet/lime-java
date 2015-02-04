package org.limeprotocol.network.tcp;

import org.junit.Test;
import org.limeprotocol.LimeUri;
import org.limeprotocol.serialization.EnvelopeSerializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TcpTransportTest {
    
    private EnvelopeSerializer envelopeSerializer;
    private TcpClient tcpClient;

    private class MockTcpClientFactory implements TcpClientFactory {

        @Override
        public TcpClient create() {
            return TcpTransportTest.this.tcpClient;
        }
    }

    private TcpTransport getTarget() {
        envelopeSerializer = mock(EnvelopeSerializer.class);
        tcpClient = mock(TcpClient.class);
        return new TcpTransport(envelopeSerializer, new MockTcpClientFactory());
    }

    @Test
    public void openAsync_notConnectedValidUri_callsConnectsAndGetStreams() throws URISyntaxException, IOException {
        // Arrange
        URI uri = DataUtil.createUri("net.tcp", 55321);

        // Act
        TcpTransport target = getTarget();
        target.open(uri);
        
        // Assert
        verify(tcpClient, times(1)).connect(new InetSocketAddress(uri.getHost(), uri.getPort()));
        verify(tcpClient, times(1)).getInputStream();
        verify(tcpClient, times(1)).getOutputStream();
    }
/*
    @Test
    public void open_validUri_connectToRemoteNode() throws URISyntaxException, IOException {
        TcpTransport tcpTransport = new TcpTransport(null, null);
        URI serverUri = new URI("net.tcp://takenet-iris.cloudapp.net:55321");
        tcpTransport.open(serverUri);
    }

    @Test(expected = IOException.class)
    public void open_invalidUriHostName_throwsIOException() throws URISyntaxException, IOException {
        TcpTransport tcpTransport = new TcpTransport(null);
        URI serverUri = new URI("net.tcp://invalidhostname.local:55321");
        tcpTransport.open(serverUri);
    }*/
    
}