package org.limeprotocol.messaging.contents;

import org.limeprotocol.Document;
import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

/**
 * Represents a flat text content
 */
public class PlainText extends DocumentBase {
    public static final String MIME_TYPE = "text/plain";

    public PlainText() {
        super(MediaType.parse(MIME_TYPE));
    }

    public PlainText(String value) {
        this();
        this.text = value;
    }

    /**
     * Text of the message
     */
    private String text;

    public String getText() {
        return text;
    }

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

}