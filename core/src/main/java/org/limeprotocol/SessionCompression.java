package org.limeprotocol;

/**
 * Defines the valid session compression values. 
 */
public enum SessionCompression {
    /**
     * The session is not compressed.
     */
    none,

    /**
     * The session is using the GZip algorithm for compression.
     */
    gzip
}
