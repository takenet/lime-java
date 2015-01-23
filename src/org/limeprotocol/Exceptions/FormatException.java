package org.limeprotocol.Exceptions;

import java.util.IllegalFormatException;

public class FormatException extends RuntimeException {

    private String message;

    //TODO: Improve quality on exception message
    public FormatException(String msg) {
        message = msg;
    }

    public String getMessage() {
        return message;
    }
}

