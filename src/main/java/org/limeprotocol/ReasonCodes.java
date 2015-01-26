package org.limeprotocol;

/// <summary>
/// Default server reason codes
/// </summary>
public class ReasonCodes
{
    /// <summary>
    /// General error
    /// </summary>
    public final int GENERAL_ERROR = 1;
    /// <summary>
    /// General session error
    /// </summary>
    public final int SESSION_ERROR = 11;
    /// <summary>
    /// The session resource is already registered
    /// </summary>
    public final int SESSION_REGISTRATION_ERROR = 12;
    /// <summary>
    /// An authentication error occurred
    /// </summary>
    public final int SESSION_AUTHENTICATION_FAILED = 13;
    /// <summary>
    /// An error occurred while unregistering the session
    /// in the server
    /// </summary>
    public final int SESSION_UNREGISTER_FAILED = 14;
    /// <summary>
    /// The required action is invalid for
    /// current session state
    /// </summary>
    public final int SESSION_INVALID_ACTION_FOR_STATE = 15;
    /// <summary>
    /// The session negotiation has timed out
    /// </summary>
    public final int SESSION_NEGOTIATION_TIMEOUT = 16;
    /// <summary>
    /// Invalid selected negotiation options
    /// </summary>
    public final int SESSION_NEGOTIATION_INVALID_OPTIONS = 17;
    /// <summary>
    /// Invalid session mode requested
    /// </summary>
    public final int SESSION_INVALID_SESSION_MODE_REQUESTED = 18;
    /// <summary>
    /// General validation error
    /// </summary>
    public final int VALIDATION_ERROR = 21;
    /// <summary>
    /// The envelope document is null
    /// </summary>
    public final int VALIDATION_EMPTY_DOCUMENT = 22;
    /// <summary>
    /// The envelope document MIME type is invalid
    /// </summary>
    public final int VALIDATION_INVALID_RESOURCE = 23;
    /// <summary>
    /// The request status is invalid
    /// </summary>
    public final int VALIDATION_INVALID_STATUS = 24;
    /// <summary>
    /// The request identity is invalid
    /// </summary>
    public final int VALIDATION_INVALID_IDENTITY = 25;
    /// <summary>
    /// The envelope originator or destination is invalid
    /// </summary>
    public final int VALIDATION_INVALID_RECIPIENTS = 26;
    /// <summary>
    /// General authorization error
    /// </summary>
    public final int AUTHORIZATION_ERROR = 31;
    /// <summary>
    /// The sender is not authorized to send
    /// messages to the message destination
    /// </summary>
    public final int AUTHORIZATION_UNAUTHORIZED_SENDER = 32;
    /// <summary>
    /// The destination doesn't have an active
    /// account
    /// </summary>
    public final int AUTHORIZATION_DESTINATION_ACCOUNT_NOT_FOUND = 33;
    /// <summary>
    /// General routing error
    /// </summary>
    public final int ROUTING_ERROR = 41;
    /// <summary>
    /// The message destination was not found
    /// </summary>
    public final int ROUTING_DESTINATION_NOT_FOUND = 42;
    /// <summary>
    /// The message destination gateway was not found
    /// </summary>
    public final int ROUTING_GATEWAY_NOT_FOUND = 43;
    /// <summary>
    /// The message destination was not found
    /// </summary>
    public final int ROUTING_ROUTE_NOT_FOUND = 44;
    /// <summary>
    /// General dispatching error
    /// </summary>
    public final int DISPATCH_ERROR = 51;
    /// <summary>
    /// General command processing error
    /// </summary>
    public final int COMMAND_PROCESSING_ERROR = 61;
    /// <summary>
    /// There's no command processor available
    /// for process the request
    /// </summary>
    public final int COMMAND_RESOURCE_NOT_SUPPORTED = 62;
    /// <summary>
    /// The command method is not supported
    /// </summary>
    public final int COMMAND_METHOD_NOT_SUPPORTED = 63;
    /// <summary>
    /// The command method has an invalid argument value
    /// </summary>
    public final int COMMAND_INVALID_ARGUMENT = 64;
    /// <summary>
    /// The requested command is not valid for current
    /// session mode
    /// </summary>
    public final int COMMAND_INVALID_SESSION_MODE = 65;
    /// <summary>
    /// The command method was not allowed
    /// </summary>
    public final int COMMAND_NOT_ALLOWED = 66;
    /// <summary>
    /// The command resource was not found
    /// </summary>
    public final int COMMAND_RESOURCE_NOT_FOUND = 67;
    /// <summary>
    /// General message processing error
    /// </summary>
    public final int MESSAGE_PROCESSING_ERROR = 61;
    /// <summary>
    /// The message content type
    /// is not supported
    /// </summary>
    public final int MESSAGE_UNSUPPORTED_CONTENT_TYPE = 71;
    /// <summary>
    /// General gateway processing error
    /// </summary>
    public final int GATEWAY_ERROR = 81;
    /// <summary>
    /// The content type is not supported
    /// by the gateway
    /// </summary>
    public final int GATEWAY_CONTENT_TYPE_NOT_SUPPORTED = 82;
    /// <summary>
    /// The message destination was not found
    /// on gateway
    /// </summary>
    public final int GATEWAY_DESTINATION_NOT_FOUND = 83;
    /// <summary>
    /// The functionality is not supported
    /// by the gateway
    /// </summary>
    public final int GATEWAY_NOT_SUPPORTED = 84;
    /// <summary>
    /// General application processing error
    /// </summary>
    public final int APPLICATION_ERROR = 101;

}

