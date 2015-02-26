package org.limeprotocol.network;

import org.limeprotocol.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ChannelExtensions {

    public final static long DEFAULT_TIMEOUT_SECONDS = 30;

    /**
     * Sends the envelope using the appropriate method for its type.
     * @param channel
     * @param envelope
     * @throws IOException
     */
    public static void send(Channel channel, Envelope envelope) throws IOException {
        if (channel == null) {
            throw new IllegalArgumentException("channel");
        }
        if (envelope == null) {
            throw new IllegalArgumentException("envelope");
        }
        
        if (envelope instanceof Notification) {
            channel.sendNotification((Notification)envelope);
        } else if (envelope instanceof Message) {
            channel.sendMessage((Message)envelope);
        } else if (envelope instanceof Command) {
            channel.sendCommand((Command)envelope);
        } else if (envelope instanceof Session) {
            channel.sendSession((Session)envelope);
        } else {
            throw new IllegalArgumentException("Invalid or unknown envelope type");
        }
    }

    /**
     * Composes a command envelope with a get method for the specified resource.
     * @param channel
     * @param limeUri
     * @param <TResource>
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static <TResource extends Document> TResource getResource(Channel channel, final LimeUri limeUri) throws IOException, InterruptedException {
        return getResource(channel, limeUri, null);
    }

    /**
     * Composes a command envelope with a get method for the specified resource.
     * @param channel
     * @param limeUri
     * @param from
     * @param <TResource>
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static <TResource extends Document> TResource getResource(Channel channel, final LimeUri limeUri, final Node from) throws IOException, InterruptedException {
        if (channel == null) {
            throw new IllegalArgumentException("channel");
        }
        if (limeUri == null) {
            throw new IllegalArgumentException("limeUri");
        }
        
        final Command requestCommand = new Command(UUID.randomUUID()) {{
            setMethod(CommandMethod.GET);
            setFrom(from);
            setUri(limeUri);
        }};
        
        Command responseCommand = processCommand(channel, requestCommand);
        if (responseCommand.getStatus() != Command.CommandStatus.SUCCESS) {
            throw new LimeException(responseCommand.getReason());
        }
        
        return (TResource)responseCommand.getResource();
    }

    /**
     * Composes a command envelope with a set method for the specified resource.
     * @param channel
     * @param limeUri
     * @param resource
     * @param <TResource>
     * @throws IOException
     * @throws InterruptedException
     */
    public static <TResource extends Document> void setResource(Channel channel, final LimeUri limeUri, final TResource resource) throws IOException, InterruptedException {
        setResource(channel, limeUri, null, resource);
    }

    /**
     * Composes a command envelope with a set method for the specified resource.
     * @param channel
     * @param limeUri
     * @param from
     * @param resource
     * @param <TResource>
     * @throws IOException
     * @throws InterruptedException
     */
    public static <TResource extends Document> void setResource(Channel channel, final LimeUri limeUri, final Node from, final TResource resource) throws IOException, InterruptedException {
        if (channel == null) {
            throw new IllegalArgumentException("channel");
        }
        if (limeUri == null) {
            throw new IllegalArgumentException("limeUri");
        }
        if (resource == null) {
            throw new IllegalArgumentException("resource");
        }
        
        final Command requestCommand = new Command(UUID.randomUUID()) {{
            setMethod(CommandMethod.SET);
            setResource(resource);
            setFrom(from);
            setUri(limeUri);
        }};

        Command responseCommand = processCommand(channel, requestCommand);
        if (responseCommand.getStatus() != Command.CommandStatus.SUCCESS) {
            throw new LimeException(responseCommand.getReason());
        }
    }

    /**
     * Composes a command envelope with a delete method for the specified resource.
     * @param channel
     * @param limeUri
     * @throws IOException
     * @throws InterruptedException
     */
    public static void deleteResource(Channel channel, final LimeUri limeUri) throws IOException, InterruptedException {
        deleteResource(channel, limeUri, null);
    }
    
    /**
     * Composes a command envelope with a delete method for the specified resource.
     * @param channel
     * @param limeUri
     * @param from
     * @throws IOException
     * @throws InterruptedException
     */
    public static void deleteResource(Channel channel, final LimeUri limeUri, final Node from) throws IOException, InterruptedException {
        if (channel == null) {
            throw new IllegalArgumentException("channel");
        }
        if (limeUri == null) {
            throw new IllegalArgumentException("limeUri");
        }

        final Command requestCommand = new Command(UUID.randomUUID()) {{
            setMethod(CommandMethod.DELETE);
            setFrom(from);
            setUri(limeUri);
        }};

        Command responseCommand = processCommand(channel, requestCommand);
        if (responseCommand.getStatus() != Command.CommandStatus.SUCCESS) {
            throw new LimeException(responseCommand.getReason());
        }
    }
    
    /**
     * Sends a command request through the channel and awaits for the response.
     * This method synchronizes the channel calls to avoid multiple command processing.
     * @param channel
     * @param command
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static Command processCommand(Channel channel, Command command) throws IOException, InterruptedException {
        return processCommand(channel, command, DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Sends a command request through the channel and awaits for the response.
     * This method synchronizes the channel calls to avoid multiple command processing.
     * @param channel
     * @param command
     * @param timeout
     * @param timeoutTimeUnit
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static Command processCommand(Channel channel, Command command, long timeout, TimeUnit timeoutTimeUnit) throws IOException, InterruptedException {
        if (channel == null) {
            throw new IllegalArgumentException("channel");
        }

        if (channel.getState() != Session.SessionState.ESTABLISHED) {
            throw new IllegalStateException("The channel state must be established");
        }
        
        if (command == null) {
            throw new IllegalArgumentException("command");
        }
        
        if (command.getStatus() != null) {
            throw new IllegalArgumentException("The command status must not be defined");
        }

        final Command[] responseCommand = new Command[0];
        final Semaphore clientChannelSemaphore = new Semaphore(1);
        
        synchronized (channel) {
            channel.addCommandListener(new CommandChannel.CommandChannelListener() {
                @Override
                public void onReceiveCommand(Command command) {
                    responseCommand[0] = command;
                    clientChannelSemaphore.release();
                }
            }, true);

            clientChannelSemaphore.acquire();
            channel.sendCommand(command);
            clientChannelSemaphore.tryAcquire(1, timeout, timeoutTimeUnit);
        }

        return responseCommand[0];
    }
}
