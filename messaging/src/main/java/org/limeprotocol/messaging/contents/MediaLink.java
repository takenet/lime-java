package org.limeprotocol.messaging.contents;

import org.limeprotocol.MediaType;

/**
 * Represents an external link to a media content.
 */
public class MediaLink extends Link {

    public static final String MIME_TYPE = "application/vnd.lime.media-link+json";

    private MediaType type;
    private Long size;

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
}
