package org.limeprotocol.messaging.contents;

import org.limeprotocol.Document;
import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;
import org.limeprotocol.Node;

/**
 * Aggregates a list of options for selection.
 */
public class Select extends DocumentBase {
    public static final String MIME_TYPE = "application/vnd.lime.select+json";

    private String text;
    private Node destination;
    private SelectOption[] options;

    public Select() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets the select question text.
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the select question text.
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the available select options.
     * @return
     */
    public SelectOption[] getOptions() {
        return options;
    }

    /**
     * Sets the available select options.
     * @param options
     */
    public void setOptions(SelectOption[] options) {
        this.options = options;
    }
}
