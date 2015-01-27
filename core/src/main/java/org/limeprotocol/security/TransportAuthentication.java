package org.limeprotocol.security;

/**
 * Defines a transport layer
 * authentication scheme.
 */
public class TransportAuthentication extends Authentication {
    public TransportAuthentication() {
        super(AuthenticationScheme.Transport);
    }
}