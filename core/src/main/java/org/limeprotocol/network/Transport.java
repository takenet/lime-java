package org.limeprotocol.network;

import org.limeprotocol.Envelope;
import org.limeprotocol.Session;

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
     *  Add a listener for receiving envelopes with default priority
     * @param transportListener
     */
    void addListener(TransportListener transportListener);

    /**
     *  Add a listener for receiving envelopes with the specify priority
     * @param transportListener
     */
    void addListener(TransportListener transportListener, Integer priority);


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
    Session.SessionCompression[] getSupportedCompression();

    /**
     * Gets the current transport compression option.
     * @return
     */
    Session.SessionCompression getCompression();

    /**
     * Defines the compression mode for the transport.
     * @param compression
     */
    void setCompression(Session.SessionCompression compression) throws IOException;
    
    /**
     * Enumerates the supported encryption options for the transport.
     * @return
     */
    Session.SessionEncryption[] getSupportedEncryption();

    /**
     * Gets the current transport encryption option.
     * @return
     */
    Session.SessionEncryption getEncryption();

    /**
     * Defines the encryption mode for the transport.
     * @param encryption
     */
    void setEncryption(Session.SessionEncryption encryption) throws IOException;

    /**
     * Defines a envelope transport listener. 
     */
    public interface TransportListener
    {
        /**
         * Occurs when a envelope is received by the transport.
         * @param envelope
         */
        void onReceive(Envelope envelope);

        /**
         * Occurs when the channel is about to be closed. 
         */
        void onClosing();

        /**
         * Occurs after the connection was closed.
         */
        void onClosed();
    }
}
