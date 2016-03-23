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
import java.net.SocketTimeoutException;
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
    private Thread jsonListenerThread;
    private boolean isConnected;

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
        this.isConnected = false;
    }

    /**
     * Checks if the client is connected based on the last read/write operation
     * @returns
     */
    public boolean isConnected(){
        return isConnected && tcpClient != null && !tcpClient.isInputShutdown() && !tcpClient.isOutputShutdown();
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

            if (traceWriter != null && traceWriter.isEnabled()) {
                traceWriter.trace(envelopeString, TraceWriter.DataOperation.SEND);
            }
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Could not convert the serialized envelope to a UTF-8 byte array", e);
        } catch (IOException e){
            close();
            throw e;
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
        isConnected = false;
    }

    /**
     * Opens the transport connection with the specified Uri.
     *
     * @param uri
     */
    @Override
    protected void performOpen(URI uri) throws IOException {
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
        isConnected = true;

        initializeStreams();

        if (getStateListener() != null) {
            startListenerThread();
        }
    }

    /**
     * Enumerates the supported encryption options for the transport.
     *
     * @return
     */
    @Override
    public SessionEncryption[] getSupportedEncryption() {
        return new SessionEncryption[]{SessionEncryption.NONE, SessionEncryption.TLS};
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
                    try {
                        tcpClient.startTls();
                        initializeStreams();
                        if (getStateListener() != null) {
                            startListenerThread();
                        }
                    } catch (IOException e) {
                        close();
                        throw e;
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
        if(!isConnected()){
            throw new IllegalStateException("The transport is not connected");
        }
        if (isListening()) {
            throw new IllegalStateException("The input listener is already started");
        }
        jsonListener = new JsonListener(inputStream, bufferSize);
        jsonListenerThread = new Thread(jsonListener);
        jsonListenerThread.start();
    }

    private synchronized void stopListenerThread() {
        if (isListening()) {
            jsonListener.stop();
            if (jsonListenerThread != null && jsonListenerThread.isAlive()) {
                jsonListenerThread.interrupt();
            }
            jsonListener = null;
            jsonListenerThread = null;
        }
    }

    class JsonListener implements Runnable {

        private final InputStream inputStream;
        private JsonBuffer jsonBuffer;
        volatile private boolean isStopping;

        JsonListener(InputStream inputStream, int bufferSize) {
            this.inputStream = inputStream;
            jsonBuffer = new JsonBuffer(bufferSize);
        }

        @Override
        public void run() {
            try {
                while (getEnvelopeListener() != null && !isStopping() && !Thread.currentThread().isInterrupted()) {
                    Envelope envelope = null;
                    while (envelope == null) {
                        JsonBuffer.JsonBufferReadResult jsonBufferReadResult = jsonBuffer.tryExtractJsonFromBuffer();
                        if (jsonBufferReadResult.isSuccess()) {
                            String jsonString = new String(jsonBufferReadResult.getJsonBytes(), Charset.forName("UTF8"));
                            if (traceWriter != null && traceWriter.isEnabled()) {
                                traceWriter.trace(jsonString, TraceWriter.DataOperation.RECEIVE);
                            }
                            envelope = envelopeSerializer.deserialize(jsonString);
                        }
                        if (envelope == null) {
                            try {
                                int read = this.inputStream.read(jsonBuffer.getBuffer(), jsonBuffer.getBufferCurPos(), jsonBuffer.getBuffer().length - jsonBuffer.getBufferCurPos());
                                if (read == -1) {
                                    // The stream reached EOF, raise closed event.
                                    close();
                                    break;
                                }
                                jsonBuffer.increaseBufferCurPos(read);
                                if (jsonBuffer.getBufferCurPos() >= jsonBuffer.getBuffer().length) {
                                    TcpTransport.this.close();
                                    throw new BufferOverflowException("Maximum buffer size reached");
                                }
                            } catch (SocketTimeoutException e) {
                                if(!isConnected()){
                                    stop();
                                }
                            } catch (IOException e){
                                TcpTransport.this.close();
                                throw e;
                            }
                        }
                    }
                    // Check if the transport was closed
                    if (envelope == null) break;
                    raiseOnReceive(envelope);
                }
            } catch (Exception e) {
                raiseOnException(e);
            }finally {
                if (traceWriter != null && traceWriter.isEnabled()) {
                    int bytesAvailable = -1;
                    try {
                        bytesAvailable = this.inputStream.available();
                    }catch(Exception e) {}
                    traceWriter.trace(String.format("TcpTransport JsonListener thread aborted with %d bytes in internal Buffer and %d bytes in input Stream", jsonBuffer.getBuffer().length, bytesAvailable), TraceWriter.DataOperation.RECEIVE);
                }
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
