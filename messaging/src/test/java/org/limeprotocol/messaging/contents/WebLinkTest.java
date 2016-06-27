package org.limeprotocol.messaging.contents;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class WebLinkTest {

    @Test
    public void toString_WithTextAndUri_ReturnsCorrectValue() throws URISyntaxException {
        String uri = "http://localhost/testing.html";
        String text = "Look at this";

        WebLink link = new WebLink();
        link.setUri(new URI(uri));
        link.setText(text);

        assertEquals(text + " " + uri, link.toString());
    }

    @Test
    public void toString_WithUri_ReturnsCorrectValue() throws URISyntaxException {
        String uri = "http://localhost/go/";

        WebLink link = new WebLink();
        link.setUri(new URI(uri));

        assertEquals(uri, link.toString());
    }
}
