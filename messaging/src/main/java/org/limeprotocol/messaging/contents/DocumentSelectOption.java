package org.limeprotocol.messaging.contents;

import org.limeprotocol.DocumentContainer;

/**
 * Defines a option to be selected by the destination.
 */
public class DocumentSelectOption {

    private Integer order;
    private DocumentContainer label;
    private DocumentContainer value;

    public DocumentSelectOption() {
    }
    /**
     * Gets the option order number.
     * @return
     */
    public Integer getOrder() {
        return order;
    }

    /**
     * Sets the option order number.
     * @param order
     */
    public void setOrder(Integer order) {
        this.order = order;
    }

    /**
     * Gets the option label document.
     * @return
     */
    public DocumentContainer getLabel() {
        return label;
    }

    /**
     * Sets the option label document.
     * @param label
     */
    public void setLabel(DocumentContainer label) {
        this.label = label;
    }

    /**
     * Gets the option value to be returned to the caller.
     * If not defined, no value should be returned.
     * @return
     */
    public DocumentContainer getValue() {
        return value;
    }

    /**
     * Sets the option value to be returned to the caller.
     * If not defined, no value should be returned.
     * @param value
     */
    public void setValue(DocumentContainer value) {
        this.value = value;
    }
}
