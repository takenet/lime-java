package org.limeprotocol.security;

/**
 * Defines a guest authentication scheme
 */
public class GuestAuthentication extends Authentication {

    public GuestAuthentication() {
        super(AuthenticationScheme.Guest);
    }
}
