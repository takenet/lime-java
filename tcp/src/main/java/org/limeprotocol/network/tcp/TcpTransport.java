package org.limeprotocol.network.tcp;

import org.limeprotocol.Envelope;
import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;
import org.limeprotocol.network.Transport;
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

/**
 * Synchronous TCP transport implementation.
 */
public class TcpTransport implements Transport {

    public final static int DEFAULT_BUFFER_SIZE = 8192;
    private final EnvelopeSerializer envelopeSerializer;

    private Socket socket;
    private SSLSocket sslSocket;

    private BufferedInputStream inputStream;
    private BufferedOutputStream outputStream;
    private TransportListener transportListener;

    public TcpTransport(EnvelopeSerializer envelopeSerializer) {
        this.envelopeSerializer = envelopeSerializer;
    }

    /**
     * Sends an envelope to the remote node.
     *
     * @param envelope
     */
    @Override
    public synchronized void send(Envelope envelope) throws IOException {
        ensureSocketOpen();
        String envelopeString = envelopeSerializer.serialize(envelope);
        try {
            byte[] envelopeBytes = envelopeString.getBytes("UTF-8");
            outputStream.write(envelopeBytes);
            outputStream.flush();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Could not convert the serialized envelope to a UTF-8 byte array", e);
        }
    }

    /**
     * Sets the listener for receiving envelopes.
     *
     * @param transportListener
     */
    @Override
    public void setListener(TransportListener transportListener) {
        this.transportListener = transportListener;
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
        
        if (socket != null) {
            throw new IllegalStateException("The client was already open");
        }
        
        socket = new Socket();
        socket.connect(new InetSocketAddress(uri.getHost(), uri.getPort()));
        inputStream = new BufferedInputStream(socket.getInputStream());
        outputStream = new BufferedOutputStream(socket.getOutputStream());
    }

    /**
     * Closes the connection.
     */
    @Override
    public synchronized void close() throws IOException {
        ensureSocketOpen();
        socket.close();
    }

    /**
     * Enumerates the supported compression options for the transport.
     *
     * @return
     */
    @Override
    public SessionCompression[] getSupportedCompression() {
        return new SessionCompression[0];
    }

    /**
     * Gets the current transport compression option.
     *
     * @return
     */
    @Override
    public SessionCompression getCompression() {
        return null;
    }

    /**
     * Defines the compression mode for the transport.
     *
     * @param compression
     */
    @Override
    public void setCompression(SessionCompression compression) {

    }

    /**
     * Enumerates the supported encryption options for the transport.
     *
     * @return
     */
    @Override
    public SessionEncryption[] getSupportedEncryption() {
        return new SessionEncryption[0];
    }

    /**
     * Gets the current transport encryption option.
     *
     * @return
     */
    @Override
    public SessionEncryption getEncryption() {
        return null;
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
                sslSocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(
                        socket,
                        socket.getInetAddress().getHostAddress(),
                        socket.getPort(),
                        true);
                
                break;
        }
    }


    private void ensureSocketOpen() {
        if (socket == null) {
            throw new IllegalStateException("The client is not open");
        }
    }
}
