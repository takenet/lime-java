package org.limeprotocol;

/**
 * Represents a generic container for a document, providing a media type for the correct handling of its value by the nodes.
 * This class can be used along with the DocumentCollection class to traffic different document types in a single message.
 */
public class DocumentContainer extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.lime.container+json";

    private Document value;

    public DocumentContainer() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets the media type of the contained document.
     * @return
     */
    public MediaType getType() {
        if (value != null) return value.getMediaType();
        return null;
    }

    /**
     * Gets the contained document value.
     * @return
     */
    public Document getValue() {
        return value;
    }

    /**
     * Sets the contained document value.
     * @param value
     */
    public void setValue(Document value) {
        this.value = value;
    }
}
