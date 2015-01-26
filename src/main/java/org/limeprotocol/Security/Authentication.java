package org.limeprotocol.security;


/// <summary>
/// Base class for the supported
/// authentication schemes
/// </summary>

public abstract class Authentication {
    private AuthenticationScheme authenticationScheme;

    public Authentication(AuthenticationScheme scheme) {
        authenticationScheme = scheme;
    }

    public AuthenticationScheme getAuthenticationScheme() {
        return authenticationScheme;
    }

    /// <summary>
    /// Defines the valid authentication schemes values.
    /// </summary>
    public enum AuthenticationScheme {
        /// <summary>
        /// The server doesn't requires a client credential,
        /// and provides a temporary identity to the node.
        /// Some restriction may apply to guest sessions,
        /// like the inability of sending some commands or
        /// other nodes may want to block messages originated
        /// by guest identities.
        /// </summary>
        Guest,
        /// <summary>
        /// Username and password authentication.
        /// </summary>
        Plain,

        /// <summary>
        /// Transport layer authentication.
        /// </summary>
        Transport
    }
}



