package org.limeprotocol.security;


/**
 * Base class for the supported
 * authentication schemes
 */

public abstract class Authentication {
    private AuthenticationScheme authenticationScheme;

    public Authentication(AuthenticationScheme scheme) {
        authenticationScheme = scheme;
    }

    public AuthenticationScheme getAuthenticationScheme() {
        return authenticationScheme;
    }

    /**
     * Defines the valid authentication schemes values.
     */
    public enum AuthenticationScheme {
        /**
         * The server doesn't requires a client credential,
         * and provides a temporary identity to the node.
         * Some restriction may apply to guest sessions,
         * like the inability of sending some commands or
         * other nodes may want to block messages originated
         * by guest identities.
         */
        GUEST,
        /**
         *Username and password authentication.
         */
        PLAIN,

        /**
         *Transport layer authentication.
         */
        TRANSPORT,
        /**
         * Key authentication.
         */
        KEY
    }
}





