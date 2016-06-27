package org.limeprotocol.messaging.contents;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class MediaLinkTest {

    @Test
    public void toString_WithTextAndValue_ReturnsCorrectValue() throws URISyntaxException {
        String uri = "http://localhost/testing.jpg";
        String text = "This is a photo";

        MediaLink link = new MediaLink();
        link.setUri(new URI(uri));
        link.setText(text);

        assertEquals(text + " " + uri, link.toString());
    }

    @Test
    public void toString_WithValue_ReturnsCorrectValue() throws URISyntaxException {
        String uri = "http://localhost/testing.jpg";

        MediaLink link = new MediaLink();
        link.setUri(new URI(uri));

        assertEquals(uri, link.toString());
    }
}
