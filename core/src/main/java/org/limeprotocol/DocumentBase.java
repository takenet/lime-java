package org.limeprotocol;

public class DocumentBase implements Document {
    MediaType mediaType;

    public DocumentBase(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public MediaType getMediaType() {
        return this.mediaType;
    }
}
