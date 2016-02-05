package org.limeprotocol.network.tcp;

import org.limeprotocol.Envelope;
import org.limeprotocol.SessionEncryption;
import org.limeprotocol.network.JsonBuffer;
import org.limeprotocol.network.TraceWriter;
import org.limeprotocol.network.Transport;
import org.limeprotocol.network.TransportBase;
import org.limeprotocol.serialization.EnvelopeSerializer;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;

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
    private JsonListener jsonListener;


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
        
        try {
            byte[] envelopeBytes = envelopeString.getBytes("UTF-8");
            outputStream.write(envelopeBytes);
            outputStream.flush();
            
            if (traceWriter != null &&
                    traceWriter.isEnabled()) {
                traceWriter.trace(envelopeString, TraceWriter.DataOperation.SEND);
            }
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
        if (getStateListener() != null) {
            startListenerThread();
        }
    }

    @Override
    public void setEnvelopeListener(TransportEnvelopeListener listener) {
        super.setEnvelopeListener(listener);
        if (listener != null && isSocketOpen() && !isListening()) {
            try {
                startListenerThread();
            } catch (IOException e) {
                throw new RuntimeException("An error occurred while starting the listener task", e);
            }
        }
    }

    @Override
    protected void performClose() throws IOException {
        stopListenerThread();
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
                    stopListenerThread();
                    tcpClient.startTls();
                    initializeStreams();
                    if (getStateListener() != null) {
                        startListenerThread();
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
        return jsonListener != null && !jsonListener.isStopping();
    }
    
    private synchronized void startListenerThread() throws IOException {
        ensureSocketOpen();
        if (isListening()) {
            throw new IllegalStateException("The input listener is already started");
        }

        jsonListener = new JsonListener();
        Thread listenerThread = new Thread(jsonListener);
        listenerThread.start();
    }
    
    private synchronized void stopListenerThread() {
        if (isListening()) {
            jsonListener.stop();
            jsonListener = null;
        }
    }

    private static int globalId;
    
    class JsonListener implements Runnable {

        // final reference of the inputStream
        private final InputStream inputStream;
        private int id = globalId++;
        private boolean isStopping;
        private JsonBuffer jsonBuffer;
        
        JsonListener() {
            this.inputStream = TcpTransport.this.inputStream;
            jsonBuffer = new JsonBuffer(bufferSize);
        }

        @Override
        public void run() {
            try {
                while (getEnvelopeListener() != null && !isStopping()) {
                    Envelope envelope = null;
                    while (envelope == null) {
                        JsonBuffer.JsonBufferReadResult jsonBufferReadResult = jsonBuffer.tryExtractJsonFromBuffer();
                        if (jsonBufferReadResult.isSuccess()) {
                            String jsonString = new String(jsonBufferReadResult.getJsonBytes(), Charset.forName("UTF8"));
                            if (traceWriter != null &&
                                    traceWriter.isEnabled()) {
                                traceWriter.trace(jsonString, TraceWriter.DataOperation.RECEIVE);
                            }
                            envelope = envelopeSerializer.deserialize(jsonString);
                        }
                        if (envelope == null) {
                            int read = this.inputStream.read(jsonBuffer.getBuffer(), jsonBuffer.getBufferCurPos(), jsonBuffer.getBuffer().length - jsonBuffer.getBufferCurPos());
                            jsonBuffer.increaseBufferCurPos(read);
                            if (jsonBuffer.getBufferCurPos() >= jsonBuffer.getBuffer().length) {
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
            
            this.isStopping = true;
        }

        public boolean isStopping() {
            return this.isStopping;
        }
        
        public void stop() {
            this.isStopping = true;
        }
        
    }
}
