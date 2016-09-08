package org.limeprotocol.messaging.contents;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.DocumentContainer;
import org.limeprotocol.MediaType;

/**
 * Represents an input request to an user.
 */
public class Input extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.lime.input+json";

    private DocumentContainer label;
    private InputValidation validation;

    public Input() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets the input label that should be shown to the user.
     * @return
     */
    public DocumentContainer getLabel() {
        return label;
    }

    /**
     * Sets the input label that should be shown to the user.
     * @param label
     */
    public void setLabel(DocumentContainer label) {
        this.label = label;
    }

    /**
     * Gets the validation rules to be enforced into the user response message for the input.
     * @return
     */
    public InputValidation getValidation() {
        return validation;
    }

    /**
     * Sets the validation rules to be enforced into the user response message for the input.
     * @param validation
     */
    public void setValidation(InputValidation validation) {
        this.validation = validation;
    }

    @Override
    public String toString() {
        return getLabel() != null ? getLabel().toString() : "";
    }
}

