package org.limeprotocol.messaging.contents;

import org.limeprotocol.MediaType;

/**
 * Provide validation rules for inputs.
 */
public class InputValidation {

    private InputValidationRule rule;
    private String regex;
    private MediaType type;
    private String error;

    /**
     * Gets the validation rule to be used.
     * @return
     */
    public InputValidationRule getRule() {
        return rule;
    }

    /**
     * Sets the validation rule to be used.
     * @param rule
     */
    public void setRule(InputValidationRule rule) {
        this.rule = rule;
    }

    /**
     * Gets the regular expression to be used in case of the rule value is regex.
     * @return
     */
    public String getRegex() {
        return regex;
    }

    /**
     * Sets the regular expression to be used in case of the rule value is regex.
     * @param regex
     */
    public void setRegex(String regex) {
        this.regex = regex;
    }

    /**
     * Gets the type to be used in case of the rule value is type.
     * @return
     */
    public MediaType getType() {
        return type;
    }

    /**
     * Sets the type to be used in case of the rule value is type.
     * @param type
     */
    public void setType(MediaType type) {
        this.type = type;
    }

    /**
     * Gets the error message text to be returned to the user in case of the input value is not valid accordingly to the defined rule.
     * @return
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the error message text to be returned to the user in case of the input value is not valid accordingly to the defined rule.
     * @param error
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Defines the input validation rules to be applied to the user input response message.
     */
    public enum InputValidationRule {
        /**
         * The value should be a text.
         * In this case, the type of the message should be text/plain.
         */
        TEXT,
        /**
         * The value should be a number (integer or floating point).
         * In this case, the type of the message should be text/plain.
         */
        NUMBER,
        /**
         * The value should be a date (optionally with time values).
         * In this case, the type of the message should be text/plain.
         */
        DATE,
        /**
         * The value should be validated with a regular expression.
         * In this case, the type of the message should be text/plain.
         */
        REGEX,
        /**
         * The value should be of a specific media type.
         */
        TYPE
    }
}
