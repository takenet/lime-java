package org.limeprotocol.messaging.contents;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

import java.net.URI;

public abstract class Link extends DocumentBase {

    private URI uri;
    private URI previewUri;
    private MediaType previewType;
    private String title;
    private String text;

    public Link(MediaType mediaType) {
        super(mediaType);
    }

    /**
     * Gets the link URI.
     * @return
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Sets the link URI.
     * @param uri
     */
    public void setUri(URI uri) {
        this.uri = uri;
    }

    /**
     * Gets the link preview URI.
     * It can be used to provide a smaller size representation of a website page or media, like a thumbnail image.
     * @return
     */
    public URI getPreviewUri() {
        return previewUri;
    }

    /**
     * Sets the link preview URI.
     * It can be used to provide a smaller size representation of a website page or media, like a thumbnail image.
     * @param previewUri
     */
    public void setPreviewUri(URI previewUri) {
        this.previewUri = previewUri;
    }

    /**
     * Gets the type of the link preview.
     * @return
     */
    public MediaType getPreviewType() {
        return previewType;
    }

    /**
     * Sets the type of the link preview.
     * @param previewType
     */
    public void setPreviewType(MediaType previewType) {
        this.previewType = previewType;
    }

    /**
     * Gets the link title text.
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the link title text.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the link description text.
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the link description text.
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {

        return (getTitle() == null ? "" : getTitle() + "\n") +
               (getText() == null ? "" : getText() + " ") +
               getUri().toString().trim();
    }
}
