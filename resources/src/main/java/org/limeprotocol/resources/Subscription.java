package org.limeprotocol.resources;

/// <summary>
/// Represents a resource subscription information, which allows to a node receive the
/// updated value every time when a subscribed resource is changed in the target node.
/// This is useful to receive updates of changes in other identities resources,
/// like the presence, but is possible to subscribe to a remote resource owned by the
/// caller, like a resource in a server. To be able to subscribe to another node resource,
/// the subscriber must have a get delegation for the resource in the publisher node.
/// </summary>

import org.limeprotocol.Document;
import org.limeprotocol.Identity;
import org.limeprotocol.MediaType;

public class Subscription implements Document {
    public final String MIME_TYPE = "application/vnd.lime.subscription+json";
    private final MediaType mediaType;

    public Subscription() {
        this.mediaType = MediaType.parse(MIME_TYPE);
    }

    /**
     * The identity of the owner of the resource.
     * The default value is the identity of the
     * from property of the envelope.
     */
    public Identity owner;

    /**
     * The MIME type of the resource for subscription.
     */
    public MediaType type;

    @Override
    public MediaType getMediaType() {
        return this.mediaType;
    }
}