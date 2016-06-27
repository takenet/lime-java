package org.limeprotocol.messaging.contents;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class SelectTest {

    @Test
    public void toString_WithText_ReturnsCorrectValue() throws URISyntaxException {
        SelectOption option1 = new SelectOption();
        option1.setText("Work");
        SelectOption option2 = new SelectOption();
        option2.setText("Leisure");
        SelectOption[] options = new SelectOption[2];
        options[0] = option1;
        options[1] = option2;

        Select select = new Select();
        select.setText("Please choose");
        select.setOptions(options);

        assertEquals("Please choose\nWork\nLeisure", select.toString());
    }

    @Test
    public void toString_WithTextAndOrder_ReturnsCorrectValue() throws URISyntaxException {
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

        assertEquals("Choose your lunch\n1. Pizza\n2. Waffle", select.toString());
    }

}
