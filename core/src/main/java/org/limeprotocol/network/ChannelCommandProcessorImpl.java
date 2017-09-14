package org.limeprotocol.network;

import org.limeprotocol.Command;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

public class ChannelCommandProcessorImpl implements ChannelCommandProcessor {

    private final ConcurrentHashMap<String, CompletableFuture<Command>> pendingCommandsMap;

    public ChannelCommandProcessorImpl() {
        pendingCommandsMap = new ConcurrentHashMap<>();
    }

    @Override
    public Command processCommand(CommandChannel commandChannel, Command requestCommand, long timeout, TimeUnit timeUnit) throws IOException, TimeoutException {
        Objects.requireNonNull(commandChannel);
        Objects.requireNonNull(requestCommand);

        if (requestCommand.getStatus() != null) {
            throw new IllegalArgumentException("Invalid command status");
        }

        if (requestCommand.getMethod() == Command.CommandMethod.OBSERVE) {
            throw new IllegalArgumentException("Invalid command method");
        }

        if (requestCommand.getId() == null) {
            throw new IllegalArgumentException("Invalid command id");
        }

        if (pendingCommandsMap.containsKey(requestCommand.getId())) {
            throw new IllegalArgumentException("Could not register the pending command request. The command id is already in use.");
        }

        CompletableFuture<Command> commandFuture = new CompletableFuture<>();
        pendingCommandsMap.put(requestCommand.getId(), commandFuture);

        commandChannel.sendCommand(requestCommand);
        try {
            return commandFuture.get(timeout, timeUnit);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean trySubmitCommandResult(Command responseCommand) {
        Objects.requireNonNull(responseCommand);

        if (responseCommand.getId() == null
                || responseCommand.getStatus() == null
                || responseCommand.getMethod() == Command.CommandMethod.OBSERVE) {
            return false;
        }

        CompletableFuture<Command> pendingRequestCommand = pendingCommandsMap.get(responseCommand.getId());
        if (pendingRequestCommand == null) return false;

        return pendingRequestCommand.complete(responseCommand);
    }

    @Override
    public void cancelAll() {
        for (Iterator<Map.Entry<String, CompletableFuture<Command>>> it = pendingCommandsMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, CompletableFuture<Command>> entry = it.next();
            entry.getValue().cancel(true);
            it.remove();
        }
    }
}
