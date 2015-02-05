package org.limeprotocol.network.tcp;

import org.junit.Test;
import org.limeprotocol.Envelope;
import org.limeprotocol.network.TraceWriter;
import org.limeprotocol.network.Transport;
import org.limeprotocol.serialization.EnvelopeSerializer;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TcpTransportTest {
    
    private EnvelopeSerializer envelopeSerializer;
    private TcpClient tcpClient;
    private TraceWriter traceWriter;

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
        traceWriter = mock(TraceWriter.class);
        return new TcpTransport(envelopeSerializer, new MockTcpClientFactory(), traceWriter);
    }

    private TcpTransport getTarget(InputStream inputStream, OutputStream outputStream) throws IOException {
        return getTarget(inputStream, outputStream, TcpTransport.DEFAULT_BUFFER_SIZE);
    }

    private TcpTransport getTarget(InputStream inputStream, OutputStream outputStream, int bufferSize) throws IOException {
        envelopeSerializer = mock(EnvelopeSerializer.class);
        tcpClient = mock(TcpClient.class);
        when(tcpClient.getOutputStream()).thenReturn(outputStream);
        when(tcpClient.getInputStream()).thenReturn(inputStream);
        traceWriter = mock(TraceWriter.class);
        return new TcpTransport(envelopeSerializer, new MockTcpClientFactory(), traceWriter, bufferSize);
    }
    
    private TcpTransport getAndOpenTarget() throws IOException, URISyntaxException {
        TcpTransport target = getTarget();
        target.open(Dummy.createUri());
        return target;
    }
    private TcpTransport getAndOpenTarget(InputStream inputStream, OutputStream outputStream) throws IOException, URISyntaxException {
        TcpTransport target = getTarget(inputStream, outputStream);
        target.open(Dummy.createUri());
        return target;
    }
    

    @Test
    public void open_notConnectedValidUri_callsConnectsAndGetStreams() throws URISyntaxException, IOException {
        // Arrange
        URI uri = Dummy.createUri();
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
        URI uri = Dummy.createUri("http", 55321);
        TcpTransport target = getTarget();
        
        // Act
        target.open(uri);
    }

    @Test(expected = IllegalStateException.class)
    public void open_alreadyConnected_throwsIllegalStateException() throws URISyntaxException, IOException {
        // Arrange
        URI uri = Dummy.createUri("net.tcp", 55321);
        TcpTransport target = getAndOpenTarget();
        
        // Act
        target.open(uri);
    }

    @Test
    public void send_validArgumentsAndOpenStreamAndTraceEnabled_callsWriteAndTraces() throws IOException, URISyntaxException {
        // Arrange
        final boolean[] outputStreamFlushed = {false};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream() {
            @Override
            public void flush() throws IOException {
                super.flush();
                outputStreamFlushed[0] = true;
            }
        };
        TcpTransport target = getAndOpenTarget(new ByteArrayInputStream(new byte[0]), outputStream);

        Envelope envelope = mock(Envelope.class);
        String serializedEnvelope = Dummy.createRandomString(200);
        when(envelopeSerializer.serialize(envelope)).thenReturn(serializedEnvelope);
        when(traceWriter.isEnabled()).thenReturn(true);

        // Act
        target.send(envelope);
        
        // Assert
        assertEquals(serializedEnvelope, outputStream.toString());
        assertTrue(outputStreamFlushed[0]);
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

    @Test
    public void onReceive_oneRead_readEnvelopeJsonFromStream() throws IOException, URISyntaxException, InterruptedException {
        // Arrange
        String messageJson = Dummy.createMessageJson();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(messageJson.getBytes("UTF-8"));
        TcpTransport target = getTarget(inputStream, new ByteArrayOutputStream());
        Envelope envelope = mock(Envelope.class);
        when(envelopeSerializer.deserialize(messageJson)).thenReturn(envelope);
        Transport.TransportListener transportListener = mock(Transport.TransportListener.class);
        target.addListener(transportListener);
        
        // Act
        target.open(Dummy.createUri());
        Thread.sleep(100);
        
        // Assert
        verify(transportListener, times(1)).onReceive(envelope);
        verify(transportListener, never()).onException(any(Exception.class));
    }

    @Test
    public void onReceive_multipleReads_readEnvelopeJsonFromStream() throws IOException, URISyntaxException, InterruptedException {
        // Arrange
        String messageJson = Dummy.createMessageJson();
        byte[] messageBuffer = messageJson.getBytes("UTF-8");
        Envelope envelope = mock(Envelope.class);
        byte[][] messageBufferParts = splitBuffer(messageBuffer);
        int bufferSize = messageBuffer.length + Dummy.createRandomInt(1000);
        TestInputStream inputStream = new TestInputStream(messageBufferParts);
        TcpTransport target = getTarget(inputStream, new ByteArrayOutputStream(), bufferSize);
        when(envelopeSerializer.deserialize(messageJson)).thenReturn(envelope);
        Transport.TransportListener transportListener = mock(Transport.TransportListener.class);
        target.addListener(transportListener);

        // Act
        target.open(Dummy.createUri());
        Thread.sleep(100);

        // Assert
        verify(transportListener, times(1)).onReceive(envelope);
        verify(transportListener, never()).onException(any(Exception.class));
        assertEquals(messageBufferParts.length, inputStream.getReadCount());
    }

    @Test
    public void onReceive_multipleReadsMultipleEnvelopes_readEnvelopesJsonFromStream() throws IOException, URISyntaxException, InterruptedException {
        // Arrange
        int messagesCount = Dummy.createRandomInt(100) + 1;
        final Queue<String> messageJsonQueue = new LinkedBlockingQueue<>();
        StringBuilder messagesJsonBuilder = new StringBuilder();
        for (int i = 0; i < messagesCount; i++) {
            String messageJson;
            do {
                messageJson = Dummy.createMessageJson();
            } while (messageJsonQueue.contains(messageJson));
            messageJsonQueue.add(messageJson);
            messagesJsonBuilder.append(messageJson);
        }
        String messagesJson = messagesJsonBuilder.toString();
        byte[] messageBuffer = messagesJson.getBytes("UTF-8");
        byte[][] messageBufferParts = splitBuffer(messageBuffer);
        int bufferSize = messageBuffer.length + Dummy.createRandomInt(1000);
        TestInputStream inputStream = new TestInputStream(messageBufferParts);
        TcpTransport target = getTarget(inputStream, new ByteArrayOutputStream(), bufferSize);
        when(envelopeSerializer.deserialize(anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (messageJsonQueue.peek().equals(invocationOnMock.getArguments()[0])) {
                    messageJsonQueue.remove();
                    return mock(Envelope.class);
                }
                return null;
            }
        });
        Transport.TransportListener transportListener = mock(Transport.TransportListener.class);
        target.addListener(transportListener);
        
        // Act
        target.open(Dummy.createUri());
        Thread.sleep(100);
        
        // Assert
        verify(transportListener, times(messagesCount)).onReceive(any(Envelope.class));
        verify(transportListener, never()).onException(any(Exception.class));
        assertEquals(messageBufferParts.length, inputStream.getReadCount());
        assertTrue(messageJsonQueue.isEmpty());
    }
    
    @Test
    public void onReceive_multipleReadsMultipleEnvelopesWithInvalidCharsBetween_readEnvelopesJsonFromStream() throws IOException, URISyntaxException, InterruptedException {
        // Arrange
        int messagesCount = Dummy.createRandomInt(100) + 1;
        final Queue<String> messageJsonQueue = new LinkedBlockingQueue<>();
        StringBuilder messagesJsonBuilder = new StringBuilder();
        messagesJsonBuilder.append("  \t\t ");
        for (int i = 0; i < messagesCount; i++) {
            String messageJson;
            do {
                messageJson = Dummy.createMessageJson();
            } while (messageJsonQueue.contains(messageJson));
            messageJsonQueue.add(messageJson);
            messagesJsonBuilder.append(messageJson);
            messagesJsonBuilder.append("\r\n   ");
        }
        String messagesJson = messagesJsonBuilder.toString();
        byte[] messageBuffer = messagesJson.getBytes("UTF-8");
        byte[][] messageBufferParts = splitBuffer(messageBuffer);
        int bufferSize = messageBuffer.length + Dummy.createRandomInt(1000);
        TestInputStream inputStream = new TestInputStream(messageBufferParts);
        TcpTransport target = getTarget(inputStream, new ByteArrayOutputStream(), bufferSize);
        when(envelopeSerializer.deserialize(anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (messageJsonQueue.peek().equals(invocationOnMock.getArguments()[0])) {
                    messageJsonQueue.remove();
                    return mock(Envelope.class);
                }
                return null;
            }
        });
        Transport.TransportListener transportListener = mock(Transport.TransportListener.class);
        target.addListener(transportListener);

        // Act
        target.open(Dummy.createUri());
        Thread.sleep(100);
        
        // Assert
        verify(transportListener, times(messagesCount)).onReceive(any(Envelope.class));
        verify(transportListener, never()).onException(any(Exception.class));
        assertEquals(messageBufferParts.length, inputStream.getReadCount());
        assertTrue(messageJsonQueue.isEmpty());
    }

    @Test
    public void onReceive_multipleReadsBiggerThenBuffer_closesTheTransportAndCallsOnException() throws IOException, URISyntaxException, InterruptedException {
        // Arrange
        String messageJson = Dummy.createMessageJson();
        byte[] messageBuffer = messageJson.getBytes("UTF-8");
        byte[][] messageBufferParts = splitBuffer(messageBuffer);
        int bufferSize = messageBuffer.length - 1;
        TestInputStream inputStream = new TestInputStream(messageBufferParts);
        TcpTransport target = getTarget(inputStream, new ByteArrayOutputStream(), bufferSize);
        when(envelopeSerializer.deserialize(anyString())).thenReturn(mock(Envelope.class));
        Transport.TransportListener transportListener = mock(Transport.TransportListener.class);
        target.addListener(transportListener);

        // Act
        target.open(Dummy.createUri());
        Thread.sleep(100);

        // Assert
        verify(transportListener, times(1)).onException(any(BufferOverflowException.class));
    }
    
    private byte[][] splitBuffer(byte[] messageBuffer) {
        int bufferParts = Dummy.createRandomInt(10) + 1;

        byte[][] messageBufferParts = new byte[bufferParts][];
        int bufferPartSize = messageBuffer.length / bufferParts;
        for (int i = 0; i < bufferParts; i++) {
            if (i + 1 == bufferParts) {
                messageBufferParts[i] = new byte[messageBuffer.length - i * bufferPartSize];
            }
            else {
                messageBufferParts[i] = new byte[bufferPartSize];
            }
            System.arraycopy(messageBuffer, i * bufferPartSize, messageBufferParts[i], 0, messageBufferParts[i].length);
        }
        return messageBufferParts;
    }
    
    public class TestInputStream extends InputStream {

        private final byte[][] buffers;
        private byte[] currentBuffer;
        private int readCount;
        private int position;

        public TestInputStream(byte[][] buffers) {
            this.buffers = buffers;
        }
        
        @Override
        public int read() throws IOException {
            return currentBuffer[position++];
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (readCount >= buffers.length) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { }
                return  0;
            }
            currentBuffer = buffers[readCount];
            readCount++;

            System.arraycopy(currentBuffer, 0, b, off, currentBuffer.length > len ? len : currentBuffer.length);
            position += currentBuffer.length;
            return currentBuffer.length;
        }


        public int getReadCount() {
            return readCount;
        }
    }
    
}