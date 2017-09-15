package org.limeprotocol.network;

import org.limeprotocol.Command;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Defines a command processor, that allows sending a command request and awaits for a response.
 */
public interface CommandProcessor {
    /**
     * Processes a command request, awaiting for the response.
     * @param requestCommand
     * @return
     */
    Command processCommand(Command requestCommand, long timeout, TimeUnit timeoutTimeUnit) throws IOException, TimeoutException, InterruptedException;
}
