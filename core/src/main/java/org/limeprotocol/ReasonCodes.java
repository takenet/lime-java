package org.limeprotocol;

/**
 * Default server reason codes
 */
public class ReasonCodes {
    /**
     * General error
     */
    public static final int GENERAL_ERROR = 1;
    /**
     * General session error
     */
    public static final int SESSION_ERROR = 11;
    /**
     * The session resource is already registered
     */
    public static final int SESSION_REGISTRATION_ERROR = 12;
    /**
     * An authentication error occurred
     */
    public static final int SESSION_AUTHENTICATION_FAILED = 13;
    /**
     * An error occurred while unregistering the session
     * in the server
     */
    public static final int SESSION_UNREGISTER_FAILED = 14;
    /**
     * The required action is invalid for
     * current session state
     */
    public static final int SESSION_INVALID_ACTION_FOR_STATE = 15;
    /**
     * The session negotiation has timed out
     */
    public static final int SESSION_NEGOTIATION_TIMEOUT = 16;
    /**
     * Invalid selected negotiation options
     */
    public static final int SESSION_NEGOTIATION_INVALID_OPTIONS = 17;
    /**
     * Invalid session mode requested
     */
    public static final int SESSION_INVALID_SESSION_MODE_REQUESTED = 18;
    /**
     * General validation error
     */
    public static final int VALIDATION_ERROR = 21;
    /**
     * The envelope document is null
     */
    public static final int VALIDATION_EMPTY_DOCUMENT = 22;
    /**
     * The envelope document MIME type is invalid
     */
    public static final int VALIDATION_INVALID_RESOURCE = 23;
    /**
     * The request status is invalid
     */
    public static final int VALIDATION_INVALID_STATUS = 24;
    /**
     * The request identity is invalid
     */
    public static final int VALIDATION_INVALID_IDENTITY = 25;
    /**
     * The envelope originator or destination is invalid
     */
    public static final int VALIDATION_INVALID_RECIPIENTS = 26;
    /**
     * General authorization error
     */
    public static final int AUTHORIZATION_ERROR = 31;
    /**
     * The sender is not authorized to send
     * messages to the message destination
     */
    public static final int AUTHORIZATION_UNAUTHORIZED_SENDER = 32;
    /**
     * The destination doesn't have an active
     * account
     */
    public static final int AUTHORIZATION_DESTINATION_ACCOUNT_NOT_FOUND = 33;
    /**
     * General routing error
     */
    public static final int ROUTING_ERROR = 41;
    /**
     * The message destination was not found
     */
    public static final int ROUTING_DESTINATION_NOT_FOUND = 42;
    /**
     * The message destination gateway was not found
     */
    public static final int ROUTING_GATEWAY_NOT_FOUND = 43;
    /**
     * The message destination was not found
     */
    public static final int ROUTING_ROUTE_NOT_FOUND = 44;
    /**
     * General dispatching error
     */
    public static final int DISPATCH_ERROR = 51;
    /**
     * General command processing error
     */
    public static final int COMMAND_PROCESSING_ERROR = 61;
    /**
     * There's no command processor available
     * for process the request
     */
    public static final int COMMAND_RESOURCE_NOT_SUPPORTED = 62;
    /**
     * The command method is not supported
     */
    public static final int COMMAND_METHOD_NOT_SUPPORTED = 63;
    /**
     * The command method has an invalid argument value
     */
    public static final int COMMAND_INVALID_ARGUMENT = 64;
    /**
     * The requested command is not valid for current
     * session mode
     */
    public static final int COMMAND_INVALID_SESSION_MODE = 65;
    /**
     * The command method was not allowed
     */
    public static final int COMMAND_NOT_ALLOWED = 66;
    /**
     * The command resource was not found
     */
    public static final int COMMAND_RESOURCE_NOT_FOUND = 67;
    /**
     * General message processing error
     */
    public static final int MESSAGE_PROCESSING_ERROR = 61;
    /**
     * The message content type
     * is not supported
     */
    public static final int MESSAGE_UNSUPPORTED_CONTENT_TYPE = 71;
    /**
     * General gateway processing error
     */
    public static final int GATEWAY_ERROR = 81;
    /**
     * The content type is not supported
     * by the gateway
     */
    public static final int GATEWAY_CONTENT_TYPE_NOT_SUPPORTED = 82;
    /**
     * The message destination was not found
     * on gateway
     */
    public static final int GATEWAY_DESTINATION_NOT_FOUND = 83;
    /**
     * The functionality is not supported
     * by the gateway
     */
    public static final int GATEWAY_NOT_SUPPORTED = 84;
    /**
     * General application processing error
     */
    public static final int APPLICATION_ERROR = 101;

}

