package org.limeprotocol.exceptions;

public class ArgumentNullException extends IllegalArgumentException {

    //TODO: Improve quality on exception message
    public ArgumentNullException(String arg) {
        super(arg);
    }

}
