package org.limeprotocol.messaging.contents;

import org.limeprotocol.Document;
import org.limeprotocol.MediaType;

/**
 *  Defines a option to be selected by the destination.
 */
public class SelectOption {

    private Integer order;
    private String text;
    private Document value;

    public SelectOption() {

    }
    /**
     * Gets or sets the option order number.
     * @return
     */
    public Integer getOrder() {
        return order;
    }

    /**
     * Gets or sets the option order number.
     * @param order
     */
    public void setOrder(Integer order) {
        this.order = order;
    }

    /**
     * Gets or sets the option label text.
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * Gets or sets the option label text.
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the media type of the option value.
     * @return
     */
    public MediaType getType() {
        Document document = getValue();
        if (document != null) return document.getMediaType();
        return null;
    }

    /**
     * Gets the option value to be returned to the caller.
     * If not defined, the value of Order (if defined) or Text should be returned.
     * @return
     */
    public Document getValue() {
        return value;
    }

    /**
     * Sets the option value to be returned to the caller.
     * If not defined, the value of Order (if defined) or Text should be returned.
     * @param value
     */
    public void setValue(Document value) {
        this.value = value;
    }
}