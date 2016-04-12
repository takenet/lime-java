package org.limeprotocol.messaging.contents;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

import java.net.URI;

/**
 * Represents an external link to a media content.
 */
public class MediaLink extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.lime.media-link+json";

    private MediaType type;
    private URI uri;
    private Long size;
    private URI previewUri;
    private MediaType previewType;
    private String text;

    public MediaLink() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets the media type of the linked media.
     * @return
     */
    public MediaType getType() {
        return type;
    }

    /**
     * Sets the media type of the linked media.
     * @param type
     */
    public void setType(MediaType type) {
        this.type = type;
    }

    /**
     * Gets the media URI.
     * @return
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Sets the media URI.
     * @param uri
     */
    public void setUri(URI uri) {
        this.uri = uri;
    }

    /**
     * Gets the media size, in bytes.
     * @return
     */
    public Long getSize() {
        return size;
    }

    /**
     * Sets the media size, in bytes.
     * @param size
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * Gets the media preview URI.
     * It can be used to provide a smaller size representation of the media, like a thumbnail for a video or image.
     * @return
     */
    public URI getPreviewUri() {
        return previewUri;
    }

    /**
     * Sets the media preview URI.
     * It can be used to provide a smaller size representation of the media, like a thumbnail for a video or image.
     * @param previewUri
     */
    public void setPreviewUri(URI previewUri) {
        this.previewUri = previewUri;
    }

    /**
     * Gets the type of the media preview.
     * @return
     */
    public MediaType getPreviewType() {
        return previewType;
    }

    /**
     * Sets the type of the media preview.
     * @param previewType
     */
    public void setPreviewType(MediaType previewType) {
        this.previewType = previewType;
    }

    /**
     * Gets the media description text.
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the media description text.
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }
}
