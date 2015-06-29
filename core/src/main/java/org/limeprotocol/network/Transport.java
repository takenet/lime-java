package org.limeprotocol.network;

import org.limeprotocol.Envelope;
import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;

import java.io.IOException;
import java.net.URI;

/**
 * Defines a network connection with a node.
 */
public interface Transport {
    
    /**
     * Sends an envelope to the remote node. 
     * @param envelope
     */
    void send(Envelope envelope) throws IOException;

    /**
     *  Register the specified listener for receiving envelopes.
     * @param transportEnvelopeListener
     */
    void setEnvelopeListener(TransportEnvelopeListener transportEnvelopeListener);

    /**
     *  Register the specified listener for receiving state change events.
     * @param transportStateListener
     */
    void setStateListener(TransportStateListener transportStateListener);
    
    /**
     * Opens the transport connection with the specified Uri.
     * @param uri
     */
    void open(URI uri) throws IOException;

    /**
     * Closes the connection.
     */
    void close() throws IOException;

    /**
     * Enumerates the supported compression options for the transport.
     * @return
     */
    SessionCompression[] getSupportedCompression();

    /**
     * Gets the current transport compression option.
     * @return
     */
    SessionCompression getCompression();

    /**
     * Defines the compression mode for the transport.
     * @param compression
     */
    void setCompression(SessionCompression compression) throws IOException;
    
    /**
     * Enumerates the supported encryption options for the transport.
     * @return
     */
    SessionEncryption[] getSupportedEncryption();

    /**
     * Gets the current transport encryption option.
     * @return
     */
    SessionEncryption getEncryption();

    /**
     * Defines the encryption mode for the transport.
     * @param encryption
     */
    void setEncryption(SessionEncryption encryption) throws IOException;

    /**
     * Defines a envelope transport listener.
     */
    public interface TransportEnvelopeListener {
        /**
         * Occurs when a envelope is received by the transport.
         * @param envelope
         */
        void onReceive(Envelope envelope);
    }
    
    /**
     * Defines a envelope transport state listener.
     */
    public interface TransportStateListener {
        /**
         * Occurs when the transport is about to be closed.
         */
        void onClosing();

        /**
         * Occurs after the transport was closed.
         */
        void onClosed();

        /**
         * Occurs when an exception is thrown during the receive process.
         * @param e The thrown exception.
         */
        void onException(Exception e);
    }
}
