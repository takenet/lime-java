package org.limeprotocol.messaging.contents;

import org.junit.Test;
import org.limeprotocol.DocumentCollection;
import org.limeprotocol.DocumentContainer;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class DocumentCollectionTest {

    @Test
    public void toString_ReturnsCorrectValue() throws URISyntaxException {
        String uri = "http://localhost/testing.html";
        String text = "Look at this";

        WebLink link = new WebLink();
        link.setUri(new URI(uri));
        link.setText(text);
        DocumentContainer container1 = new DocumentContainer();
        container1.setValue(link);

        SelectOption option1 = new SelectOption();
        option1.setText("Pizza");
        option1.setOrder(1);
        SelectOption option2 = new SelectOption();
        option2.setText("Waffle");
        option2.setOrder(2);
        SelectOption[] options = new SelectOption[2];
        options[0] = option1;
        options[1] = option2;

        Select select = new Select();
        select.setText("Choose your lunch");
        select.setOptions(options);

        DocumentContainer container2 = new DocumentContainer();
        container2.setValue(select);

        DocumentCollection collection = new DocumentCollection();
        DocumentContainer[] items = new DocumentContainer[2];
        items[0]=container1;
        items[1]=container2;
        collection.setItems(items);

        assertEquals(text + " " + uri + "\nChoose your lunch\n1. Pizza\n2. Waffle", collection.toString());
    }
}
