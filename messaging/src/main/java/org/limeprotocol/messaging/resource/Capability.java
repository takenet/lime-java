package org.limeprotocol.messaging.resource;

import org.limeprotocol.Document;
import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

/**
 * Represents the capabilities
 * of the nodes of the network
 */
public class Capability extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.lime.capability+json";

    public Capability()
    {
        super(MediaType.parse(MIME_TYPE));
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
}


