package org.limeprotocol.resources;

import org.limeprotocol.Document;
import org.limeprotocol.MediaType;

public class Ping implements Document {

    public final String MIME_TYPE = "application/vnd.lime.ping+json";
    private final MediaType mediaType;

    public Ping() {
        this.mediaType = MediaType.parse(MIME_TYPE);
    }

    @Override
    public MediaType getMediaType() {
        return this.mediaType;
    }
}
