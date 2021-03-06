package org.limeprotocol.network;

import org.limeprotocol.Command;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface ChannelCommandProcessor {
    Command processCommand(CommandChannel commandChannel, Command requestCommand, long timeout, TimeUnit timeoutTimeUnit) throws IOException, TimeoutException, InterruptedException;

    boolean trySubmitCommandResult(Command responseCommand);

    void cancelAll();
}
