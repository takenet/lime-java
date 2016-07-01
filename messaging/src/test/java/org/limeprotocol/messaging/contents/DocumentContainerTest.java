package org.limeprotocol.messaging.contents;

import org.junit.Test;
import org.limeprotocol.DocumentContainer;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class DocumentContainerTest {

    @Test
    public void toString_WithWebLink_ReturnsCorrectValue() throws URISyntaxException {
        String uri = "http://localhost/testing.html";
        String text = "Look at this";

        WebLink link = new WebLink();
        link.setUri(new URI(uri));
        link.setText(text);
        DocumentContainer container = new DocumentContainer();
        container.setValue(link);

        assertEquals(text + " " + uri, container.toString());
    }

    @Test
    public void toString_WithPlainText_ReturnsCorrectValue() throws URISyntaxException {
        String content = "it works!";
        PlainText text = new PlainText(content);
        DocumentContainer container = new DocumentContainer();
        container.setValue(text);

        assertEquals(content, container.toString());
    }
}
