package org.limeprotocol.network.tcp;

import org.limeprotocol.Envelope;
import org.limeprotocol.SessionEncryption;
import org.limeprotocol.network.TraceWriter;
import org.limeprotocol.network.Transport;
import org.limeprotocol.network.TransportBase;
import org.limeprotocol.serialization.EnvelopeSerializer;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.concurrent.*;

/**
 * Synchronous TCP transport implementation.
 */
public class TcpTransport extends TransportBase implements Transport {

    public final static int DEFAULT_BUFFER_SIZE = 8192;
    private final EnvelopeSerializer envelopeSerializer;
    private final TcpClientFactory tcpClientFactory;
    private final TraceWriter traceWriter;
    private final int bufferSize;
    private TcpClient tcpClient;
    private BufferedOutputStream outputStream;
    private BufferedInputStream inputStream;
    private Future<?> inputListenerFuture;
    private ExecutorService executorService;

    public TcpTransport(EnvelopeSerializer envelopeSerializer, TcpClientFactory tcpClientFactory) {
        this(envelopeSerializer, tcpClientFactory, null, DEFAULT_BUFFER_SIZE);
    }
    
    public TcpTransport(EnvelopeSerializer envelopeSerializer, TcpClientFactory tcpClientFactory, TraceWriter traceWriter) {
        this(envelopeSerializer, tcpClientFactory, traceWriter, DEFAULT_BUFFER_SIZE);
    }
    
    public TcpTransport(EnvelopeSerializer envelopeSerializer, TcpClientFactory tcpClientFactory, TraceWriter traceWriter, int bufferSize) {
        this.envelopeSerializer = envelopeSerializer;
        this.tcpClientFactory = tcpClientFactory;
        this.traceWriter = traceWriter;
        this.bufferSize = bufferSize;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Sends an envelope to the remote node.
     *
     * @param envelope
     */
    @Override
    public synchronized void send(Envelope envelope) throws IOException {
        if (envelope == null) {
            throw new IllegalArgumentException("envelope");
        }
        ensureSocketOpen();
        String envelopeString = envelopeSerializer.serialize(envelope);
        
        if (traceWriter != null &&
                traceWriter.isEnabled()) {
            traceWriter.trace(envelopeString, TraceWriter.DataOperation.SEND);
        }

        try {
            byte[] envelopeBytes = envelopeString.getBytes("UTF-8");
            outputStream.write(envelopeBytes);
            outputStream.flush();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Could not convert the serialized envelope to a UTF-8 byte array", e);
        }
    }

    /**
     * Opens the transport connection with the specified Uri.
     *
     * @param uri
     */
    @Override
    public synchronized void open(URI uri) throws IOException {
        if (uri == null) {
            throw new IllegalArgumentException("uri");
        }
        
        // TODO: This is the best scheme to use?
        if (!uri.getScheme().equals("net.tcp")) {
            throw new IllegalArgumentException("Invalid URI scheme. Expected is 'net.tcp'", null);
        }
        
        if (tcpClient != null) {
            throw new IllegalStateException("The client is already open");
        }
        
        tcpClient = tcpClientFactory.create();
        tcpClient.connect(new InetSocketAddress(uri.getHost(), uri.getPort()));
        outputStream = new BufferedOutputStream(tcpClient.getOutputStream());
        inputStream = new BufferedInputStream(tcpClient.getInputStream());
        inputListenerFuture = executorService.submit(new JsonStreamReader(bufferSize));
    }

    @Override
    protected void performClose() throws IOException {
        if (tcpClient != null) {
            tcpClient.close();
        }
        if (inputListenerFuture != null &&
                !inputListenerFuture.isDone()) {
            if (!inputListenerFuture.cancel(true)) {
                throw new IllegalStateException("Could not stop the reader");
            }
        }
    }

    /**
     * Enumerates the supported encryption options for the transport.
     *
     * @return
     */
    @Override
    public SessionEncryption[] getSupportedEncryption() {
        return new SessionEncryption[] { SessionEncryption.none, SessionEncryption.tls };
    }

    /**
     * Defines the encryption mode for the transport.
     *
     * @param encryption
     */
    @Override
    public void setEncryption(SessionEncryption encryption) throws IOException {
        switch (encryption) {
            case tls:
                SSLSocket sslSocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(
                        tcpClient.getSocket(),
                        tcpClient.getSocket().getInetAddress().getHostAddress(),
                        tcpClient.getSocket().getPort(),
                        true);
                
                break;
            
        }
        
        super.setEncryption(encryption);
    }

    private void ensureSocketOpen() {
        if (tcpClient == null) {
            throw new IllegalStateException("The client is not open");
        }
    }

    class JsonStreamReader implements Callable<Void> {
        private boolean canRead;
        private byte[] buffer;
        private int bufferCurPos;
        private int jsonStartPos;
        private int jsonCurPos;
        private int jsonStackedBrackets;
        private boolean jsonStarted = false;
        
        JsonStreamReader(int bufferSize) {
            buffer = new byte[bufferSize];
            canRead = true;
        }

        @Override
        public Void call() throws Exception {
            try {
                if (TcpTransport.this.inputStream == null) {
                    throw new IllegalStateException("The stream was not initialized. Call Open first.");
                }

                while (canRead()) {
                    Envelope envelope = null;
                    while (envelope == null) {
                        JsonBufferReadResult jsonBufferReadResult = tryExtractJsonFromBuffer();
                        if (jsonBufferReadResult.isSuccess()) {
                            String jsonString = new String(jsonBufferReadResult.getJsonBytes(), Charset.forName("UTF8"));
                            envelope = TcpTransport.this.envelopeSerializer.deserialize(jsonString);
                        }

                        if (envelope == null) {
                            bufferCurPos += TcpTransport.this.inputStream.read(buffer, bufferCurPos, buffer.length - bufferCurPos);
                            if (bufferCurPos >= buffer.length) {
                                TcpTransport.this.close();
                                throw new BufferOverflowException("Maximum buffer size reached");
                            }
                        }
                    }

                    TcpTransport.this.getListenerBroadcastSender().broadcastOnReceive(envelope);
                }
            } catch (Exception e) {
                TcpTransport.this.getListenerBroadcastSender().broadcastOnException(e);
            }
            return null;
        }

        private JsonBufferReadResult tryExtractJsonFromBuffer() {
            if (bufferCurPos > buffer.length) {
                throw new IllegalArgumentException("Buffer current pos or length value is invalid", null);
            }

            byte[] json = null;
            int jsonLength = 0;
            for (int i = jsonCurPos; i < bufferCurPos; i++) {
                jsonCurPos = i + 1;

                if (buffer[i] == '{') {
                    jsonStackedBrackets++;
                    if (!jsonStarted) {
                        jsonStartPos = i;
                        jsonStarted = true;
                    }
                }
                else if (buffer[i] == '}') {
                    jsonStackedBrackets--;
                }

                if (jsonStarted && 
                        jsonStackedBrackets == 0) {
                    jsonLength = i - jsonStartPos + 1;
                    break;
                }
            }

            if (jsonLength > 1) {
                json = new byte[jsonLength];
                System.arraycopy(buffer, jsonStartPos, json, 0, jsonLength);

                // Shifts the buffer to the left
                bufferCurPos -= (jsonLength + jsonStartPos);
                System.arraycopy(buffer, jsonLength + jsonStartPos, buffer, 0, bufferCurPos);
                jsonCurPos = 0;
                jsonStartPos = 0;
                jsonStarted = false;

                return new JsonBufferReadResult(true, json);
            }

            return new JsonBufferReadResult(false, null);
        }

        public boolean canRead() {
            return canRead;
        }

        public void setCanRead(boolean canRead) {
            this.canRead = canRead;
        }

        class JsonBufferReadResult {
            private final boolean success;
            private final byte[] jsonBytes;
            
            public JsonBufferReadResult(boolean success, byte[] jsonBytes) {
                this.success = success;
                this.jsonBytes = jsonBytes;
            }

            public boolean isSuccess() {
                return success;
            }

            public byte[] getJsonBytes() {
                return jsonBytes;
            }
        }
    }    
}
