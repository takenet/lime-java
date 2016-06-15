package org.limeprotocol.messaging.contents;

import org.limeprotocol.MediaType;

/**
 * Represents an external link to a website page.
 */
public class WebLink extends Link {

    public static final String MIME_TYPE = "application/vnd.lime.web-link+json";

    private MediaType type;
    private Long size;

    public WebLink() {
        super(MediaType.parse(MIME_TYPE));
    }
}