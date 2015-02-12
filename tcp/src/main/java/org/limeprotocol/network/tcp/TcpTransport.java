package org.limeprotocol.network.tcp;

import org.limeprotocol.Envelope;
import org.limeprotocol.SessionEncryption;
import org.limeprotocol.network.TraceWriter;
import org.limeprotocol.network.Transport;
import org.limeprotocol.network.TransportBase;
import org.limeprotocol.serialization.EnvelopeSerializer;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;

import java.io.*;
import java.net.InetSocketAddress;
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
    private Future<?> listenerFuture;
    private ExecutorService executorService;

    public TcpTransport() {
        this(new JacksonEnvelopeSerializer(), new SocketTcpClientFactory(), null, DEFAULT_BUFFER_SIZE);
    }
    
    public TcpTransport(EnvelopeSerializer envelopeSerializer) {
        this(envelopeSerializer, new SocketTcpClientFactory(), null, DEFAULT_BUFFER_SIZE);
    }
    
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
        initializeStreams();
        if (hasAnyListener()) {
            startListener();
        }
    }

    @Override
    public synchronized void addListener(TransportListener listener, boolean removeAfterReceive) {
        super.addListener(listener, removeAfterReceive);
        if (isSocketOpen() && !isListening()) {
            try {
                startListener();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("An error occurred while starting the listener task", e);
            }
        }
    }

    @Override
    protected void performClose() throws IOException {
        stopListener();
        if (tcpClient != null) {
            tcpClient.close();
        }
    }

    /**
     * Enumerates the supported encryption options for the transport.
     *
     * @return
     */
    @Override
    public SessionEncryption[] getSupportedEncryption() {
        return new SessionEncryption[] { SessionEncryption.NONE, SessionEncryption.TLS };
    }

    /**
     * Defines the encryption mode for the transport.
     *
     * @param encryption
     */
    @Override
    public void setEncryption(SessionEncryption encryption) throws IOException {
        switch (encryption) {
            case TLS:
                if (!tcpClient.isTlsStarted()) {
                    stopListener();
                    tcpClient.startTls();
                    initializeStreams();
                    if (hasAnyListener()) {
                        startListener();
                    }
                }
                break;
            case NONE:
                if (tcpClient.isTlsStarted()) {
                    throw new IllegalStateException("Cannot downgrade an encrypted connection");
                }
                break;
        }
        
        super.setEncryption(encryption);
        
    }

    private boolean isSocketOpen() {
        return tcpClient != null;
    }
    
    private void ensureSocketOpen() {
        if (tcpClient == null) {
            throw new IllegalStateException("The client is not open");
        }
    }

    private void initializeStreams() throws IOException {
        outputStream = new BufferedOutputStream(tcpClient.getOutputStream());
        inputStream = new BufferedInputStream(tcpClient.getInputStream());
    }

    private boolean isListening() {
        return listenerFuture != null && !listenerFuture.isDone();
    }
    
    private synchronized void startListener() throws IOException {
        ensureSocketOpen();
        if (isListening()) {
            throw new IllegalStateException("The input listener is already started");
        }
        listenerFuture = executorService.submit(new JsonListener());
    }
    
    private synchronized void stopListener() {
        if (isListening()) {
            if (!listenerFuture.cancel(true)) {
                throw new IllegalStateException("Could not stop the input listener");
            }
            listenerFuture = null;
        }
    }

    class JsonListener implements Callable<Void> {

        // final reference of the inputStream
        private final InputStream inputStream;
        private byte[] buffer;
        private int bufferCurPos;
        private int jsonStartPos;
        private int jsonCurPos;
        private int jsonStackedBrackets;
        private boolean jsonStarted = false;

        JsonListener() {
            this.inputStream = TcpTransport.this.inputStream;
            buffer = new byte[bufferSize];
        }

        @Override
        public Void call() throws Exception {
            try {
                while (hasAnyListener()) {
                    Envelope envelope = null;
                    while (envelope == null) {
                        JsonBufferReadResult jsonBufferReadResult = tryExtractJsonFromBuffer();
                        if (jsonBufferReadResult.isSuccess()) {
                            String jsonString = new String(jsonBufferReadResult.getJsonBytes(), Charset.forName("UTF8"));
                            if (traceWriter != null &&
                                    traceWriter.isEnabled()) {
                                traceWriter.trace(jsonString, TraceWriter.DataOperation.RECEIVE);
                            }

                            envelope = envelopeSerializer.deserialize(jsonString);
                        }

                        if (envelope == null) {
                            bufferCurPos += this.inputStream.read(buffer, bufferCurPos, buffer.length - bufferCurPos);
                            if (bufferCurPos >= buffer.length) {
                                TcpTransport.this.close();
                                throw new BufferOverflowException("Maximum buffer size reached");
                            }
                        }
                    }
                    
                    raiseOnReceive(envelope);
                }
            } catch (Exception e) {
                raiseOnException(e);
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
