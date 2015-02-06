package org.limeprotocol;

import org.limeprotocol.util.StringUtils;

public class PlainDocument extends DocumentBase {

    /**
     * The value of the document
     */
    private String value;

    public PlainDocument(MediaType mediaType){
        this(null, mediaType);
    }

    public PlainDocument(String value, MediaType mediaType) {
        super(mediaType);

        if (!StringUtils.isNullOrWhiteSpace(mediaType.getSuffix()))
        {
            throw new IllegalArgumentException("Invalid media type. The suffix value should be empty.");
        }

        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString()
    {
        return this.value;
    }

    @Override
    public MediaType getMediaType() {
        return this.mediaType;
    }
}
