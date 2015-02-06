package org.limeprotocol.messaging.resource;

import org.limeprotocol.Document;
import org.limeprotocol.MediaType;

/**
 * Represents the capabilities
 * of the nodes of the network
 */
public class Capability implements Document {

    public final String MIME_TYPE = "application/vnd.lime.capability+json";

    public MediaType mediaType;

    public Capability()
    {
        this.mediaType = MediaType.parse(MIME_TYPE);
    }

    /**
     * Indicates the message content types
     * that the session node is able to handle.
     */
    private MediaType[] contentTypes;

    /**
     * Indicates the command resource types
     * that the session node is able to handle.
     */
    private MediaType[] resourceTypes;

    public MediaType[] getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(MediaType[] contentTypes) {
        this.contentTypes = contentTypes;
    }

    public MediaType[] getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(MediaType[] resourceTypes) {
        this.resourceTypes = resourceTypes;
    }

    @Override
    public MediaType getMediaType() {
        return this.mediaType;
    }
}


