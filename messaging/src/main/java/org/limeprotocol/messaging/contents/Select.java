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
     * Gets the destination which the selected option should be sent to.
     * If not defined, the selected option should be sent to the caller.
     * @return
     */
    public Node getDestination() {
        return destination;
    }

    /**
     * Sets the destination which the selected option should be sent to.
     * If not defined, the selected option should be sent to the caller.
     * @param destination
     */
    public void setDestination(Node destination) {
        this.destination = destination;
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

    /**
     *  Defines a option to be selected by the destination.
     */
    public class SelectOption {

        private Integer order;
        private String text;
        private MediaType type;
        private Document value;

        public SelectOption(){}
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
}
