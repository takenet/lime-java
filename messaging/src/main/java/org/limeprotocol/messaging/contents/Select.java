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

    private SelectScope scope;
    private String text;
    private SelectOption[] options;

    public Select() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets the scope which the select options is valid.
     * This property hints to the destination of the select when the sender is able to receive and understand a select option reply.
     * @return
     */
    public SelectScope getScope() {
        return scope;
    }

    /**
     * Sets the scope which the select options is valid.
     * This property hints to the destination of the select when the sender is able to receive and understand a select option reply.
     * @param scope
     */
    public void setScope(SelectScope scope) {
        this.scope = scope;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getText() + "\n");
        for (SelectOption option : getOptions()) {
            if (option.getOrder() != null) {
                builder.append(option.getOrder() + ". ");
            }
            builder.append(option.getText() + "\n");
        }
        return builder.toString().trim();
    }

    /**
     * Defines the scope which a select is valid.
     */
    public enum SelectScope {

        /**
         * The select is transient and its valid during a temporary conversation scope.
         * This is the default scope.
         */
        TRANSIENT,

        /**
         * The select is persistent and its valid in any time for the specific sender.
         */
        PERSISTENT,

        /**
         * The select is valid only valid for the current scope.
         */
        IMMEDIATE
    }
}
