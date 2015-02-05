package org.limeprotocol.network.tcp;

public class BufferOverflowException extends RuntimeException {
    public BufferOverflowException(String message) {
        super(message);
    }
}
