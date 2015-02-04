package org.limeprotocol.network.tcp;

import org.junit.Test;
import org.limeprotocol.Envelope;
import org.limeprotocol.network.TraceWriter;
import org.limeprotocol.serialization.EnvelopeSerializer;
import org.mockito.AdditionalMatchers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TcpTransportTest {
    
    private EnvelopeSerializer envelopeSerializer;
    private TcpClient tcpClient;
    private TraceWriter traceWriter;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean outputStreamFlushed;
    
    private class MockTcpClientFactory implements TcpClientFactory {

        @Override
        public TcpClient create() {
            return TcpTransportTest.this.tcpClient;
        }
    }

    private TcpTransport getTarget() throws IOException {
        envelopeSerializer = mock(EnvelopeSerializer.class);
        tcpClient = mock(TcpClient.class);
        inputStream = mock(InputStream.class);
        outputStream = mock(OutputStream.class);
        when(tcpClient.getInputStream()).thenReturn(inputStream);
        when(tcpClient.getOutputStream()).thenReturn(outputStream);
        traceWriter = mock(TraceWriter.class);
        return new TcpTransport(envelopeSerializer, new MockTcpClientFactory(), traceWriter);
    }
    
    private TcpTransport getAndOpenTarget() throws IOException, URISyntaxException {
        TcpTransport target = getTarget();
        target.open(DataUtil.createUri("net.tcp", 55321));
        return target;
    }

    @Test
    public void open_notConnectedValidUri_callsConnectsAndGetStreams() throws URISyntaxException, IOException {
        // Arrange
        URI uri = DataUtil.createUri("net.tcp", 55321);
        TcpTransport target = getTarget();
        
        // Act
        target.open(uri);
        
        // Assert
        verify(tcpClient, times(1)).connect(new InetSocketAddress(uri.getHost(), uri.getPort()));
        verify(tcpClient, times(1)).getInputStream();
        verify(tcpClient, times(1)).getOutputStream();
    }

    @Test(expected = IllegalArgumentException.class)
    public void open_notConnectedInvalidUriScheme_throwsIllegalArgumentException() throws URISyntaxException, IOException {
        // Arrange
        URI uri = DataUtil.createUri("http", 55321);
        TcpTransport target = getTarget();
        
        // Act
        target.open(uri);
    }

    @Test(expected = IllegalStateException.class)
    public void open_alreadyConnected_throwsIllegalStateException() throws URISyntaxException, IOException {
        // Arrange
        URI uri = DataUtil.createUri("net.tcp", 55321);
        TcpTransport target = getAndOpenTarget();
        
        // Act
        target.open(uri);
    }

    @Test
    public void send_validArgumentsAndOpenStreamAndTraceEnabled_callsWriteAndTraces() throws IOException, URISyntaxException {
        // Arrange
        TcpTransport target = getAndOpenTarget();
        Envelope envelope = mock(Envelope.class);
        String serializedEnvelope = DataUtil.createRandomString(200);
        when(envelopeSerializer.serialize(envelope)).thenReturn(serializedEnvelope);
        when(traceWriter.isEnabled()).thenReturn(true);
        
        // Act
        target.send(envelope);
        
        // Assert
        byte[] serializedEnvelopeBytes = serializedEnvelope.getBytes("UTF-8");
        byte[] expectedBuffer = new byte[8192];
        System.arraycopy(serializedEnvelopeBytes, 0, expectedBuffer, 0, serializedEnvelopeBytes.length);
        verify(outputStream, times(1)).write(expectedBuffer, 0, serializedEnvelope.length());
        verify(outputStream, atLeastOnce()).flush();
        verify(traceWriter, atLeastOnce()).trace(serializedEnvelope, TraceWriter.DataOperation.SEND);
    }

    @Test(expected = IllegalArgumentException.class)
    public void send_nullEnvelope_throwsIllegalArgumentException() throws IOException, URISyntaxException {
        // Arrange
        TcpTransport target = getAndOpenTarget();
        Envelope envelope = null;

        // Act
        target.send(envelope);
    }

    @Test(expected = IllegalStateException.class)
    public void send_closedTransport_throwsIllegalStateException() throws IOException, URISyntaxException {
        // Arrange
        TcpTransport target = getTarget();
        Envelope envelope = mock(Envelope.class);

        // Act
        target.send(envelope);
    }
    

}