package org.limeprotocol.messaging.contents;

import org.junit.Test;
import org.limeprotocol.DocumentContainer;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class DocumentSelectTest {

    @Test
    public void toString_WithText_ReturnsCorrectValue() throws URISyntaxException {
        DocumentSelectOption option1 = new DocumentSelectOption();
        option1.setLabel(new DocumentContainer(new PlainText("Work")));
        DocumentSelectOption option2 = new DocumentSelectOption();
        option2.setLabel(new DocumentContainer(new PlainText("Leisure")));
        DocumentSelectOption[] options = new DocumentSelectOption[2];
        options[0] = option1;
        options[1] = option2;

        DocumentSelect select = new DocumentSelect();
        select.setHeader(new DocumentContainer(new PlainText("Please choose")));
        select.setOptions(options);

        assertEquals("Please choose\nWork\nLeisure", select.toString());
    }

    @Test
    public void toString_WithTextAndOrder_ReturnsCorrectValue() throws URISyntaxException {
        DocumentSelectOption option1 = new DocumentSelectOption();
        option1.setLabel(new DocumentContainer(new PlainText("Pizza")));
        option1.setOrder(1);
        DocumentSelectOption option2 = new DocumentSelectOption();
        option2.setLabel(new DocumentContainer(new PlainText("Waffle")));
        option2.setOrder(2);
        DocumentSelectOption[] options = new DocumentSelectOption[2];
        options[0] = option1;
        options[1] = option2;

        DocumentSelect select = new DocumentSelect();
        select.setHeader(new DocumentContainer(new PlainText("Choose your lunch")));
        select.setOptions(options);

        assertEquals("Choose your lunch\n1. Pizza\n2. Waffle", select.toString());
    }
}
