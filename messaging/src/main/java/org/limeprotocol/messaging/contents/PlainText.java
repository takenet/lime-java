package org.limeprotocol.messaging.contents;

import org.limeprotocol.Document;
import org.limeprotocol.MediaType;

/**
 * Represents a flat text content
 */
public class PlainText implements Document {
    public final String MIME_TYPE = "text/plain";
    private MediaType mediaType;


    public PlainText() {
        this.mediaType = MediaType.parse(MIME_TYPE);
    }

    public PlainText(String value) {
        this();
        this.text = value;
    }

    /**
     * Text of the message
     */
    public String text;


    /**
     * Parses the string to a
       PlainText instance.
     * @param value
     * @return parsed PlainText
     */
    public static PlainText parse(String value) {
        return new PlainText(value);
    }


    /**
     * Returns a String that represents this instance.
     * @return
     */
    @Override
    public String toString() {
        return this.text;
    }

    @Override
    public MediaType getMediaType() {
        return this.mediaType;
    }


}