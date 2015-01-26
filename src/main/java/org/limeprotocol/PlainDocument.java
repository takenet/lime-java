package org.limeprotocol;

import org.limeprotocol.util.StringUtils;

public class PlainDocument implements Document {

    private MediaType mediaType;
    /// <summary>
    /// The value of the document
    /// </summary>
    public String value;


    public PlainDocument(MediaType mediaType){
        this(null, mediaType);
    }

    public PlainDocument(String value, MediaType mediaType)
    {
        this.mediaType = mediaType;

        if (!StringUtils.isNullOrWhiteSpace(mediaType.getSuffix()))
        {
            throw new IllegalArgumentException("Invalid media type. The suffix value should be empty.");
        }

        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.value;
    }

    @Override
    public MediaType getMediaType() {
        return null;
    }
}
