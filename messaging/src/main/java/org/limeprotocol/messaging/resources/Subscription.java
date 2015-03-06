package org.limeprotocol.messaging.resources;

import org.limeprotocol.Document;
import org.limeprotocol.DocumentBase;
import org.limeprotocol.Identity;
import org.limeprotocol.MediaType;

/**
 * Represents a resource subscription information, which allows to a node receive the
 * updated value every time when a subscribed resource is changed in the target node.
 * This is useful to receive updates of changes in other identities messaging,
 * like the presence, but is possible to subscribe to a remote resource owned by the
 * caller, like a resource in a server. To be able to subscribe to another node resource,
 * the subscriber must have a get delegation for the resource in the publisher node.
 */
public class Subscription extends DocumentBase {
    public static final String MIME_TYPE = "application/vnd.lime.subscription+json";

    public Subscription() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * The identity of the owner of the resource.
     * The default value is the identity of the
     * from property of the envelope.
     */
    private Identity owner;

    /**
     * The MIME type of the resource for subscription.
     */
    private MediaType type;

    public Identity getOwner() {
        return owner;
    }

    public void setOwner(Identity owner) {
        this.owner = owner;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }
}