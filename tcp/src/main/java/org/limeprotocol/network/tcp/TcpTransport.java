package org.limeprotocol.network.tcp;

import org.limeprotocol.Envelope;
import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;
import org.limeprotocol.network.Transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;

/**
 * Synchronous TCP transport implementation.
 */
public class TcpTransport implements Transport {

    public final static int DEFAULT_BUFFER_SIZE = 8192;
    
    private Socket socket;

    public TcpTransport() {
        
    }

    /**
     * Sends an envelope to the remote node.
     *
     * @param envelope
     */
    @Override
    public void send(Envelope envelope) {
        
    }

    /**
     * Sets the listener for receiving envelopes.
     *
     * @param transportListener
     */
    @Override
    public void setListener(TransportListener transportListener) {

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
    }

    /**
     * Closes the connection.
     */
    @Override
    public synchronized void close() throws IOException {
        if (socket == null) {
            throw new IllegalStateException("The client is not open");
        }
        
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
    public void setEncryption(SessionEncryption encryption) {

    }
}
