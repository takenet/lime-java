package org.limeprotocol.messaging.contents;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.DocumentContainer;
import org.limeprotocol.MediaType;

/**
 * Aggregates a list of DocumentSelectOption for selection.
 * This class is similar to the Select, but allows generic documents to be defined in the select header and options, instead of plain text.
 */
public class DocumentSelect extends DocumentBase {
    public static final String MIME_TYPE = "application/vnd.lime.document-select+json";

    private DocumentSelectScope scope;
    private DocumentContainer header;
    private DocumentSelectOption[] options;

    public DocumentSelect() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets the scope which the select options is valid.
     * This property hints to the destination of the select when the sender is able to receive and understand a select option reply.
     * @return
     */
    public DocumentSelectScope getScope() {
        return scope;
    }

    /**
     * Sets the scope which the select options is valid.
     * This property hints to the destination of the select when the sender is able to receive and understand a select option reply.
     * @param scope
     */
    public void setScope(DocumentSelectScope scope) {
        this.scope = scope;
    }

    /**
     * Gets the select header document.
     * @return
     */
    public DocumentContainer getHeader() {
        return header;
    }

    /**
     * Sets the select header document.
     * @param header
     */
    public void setHeader(DocumentContainer header) {
        this.header = header;
    }

    /**
     * Gets the available select options.
     * @return
     */
    public DocumentSelectOption[] getOptions() {
        return options;
    }

    /**
     * Sets the available select options.
     * @param options
     */
    public void setOptions(DocumentSelectOption[] options) {
        this.options = options;
    }

    /**
     * Defines the scope which a select is valid.
     */
    public enum DocumentSelectScope {

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

