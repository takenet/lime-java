package org.limeprotocol;

/**
 * Defines the valid session encryption values.
 */
public enum SessionEncryption {
    /**
     * The session is not encrypted.
     */
    NONE,
    /**
     * The session is encrypted by TLS (TRANSPORT Layer Security).
     */
    TLS
}
