package org.limeprotocol.network;

import org.limeprotocol.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Defines a command processor, that allows sending a command request and awaits for a response.
 */
public interface CommandProcessor {

    long DEFAULT_TIMEOUT_SECONDS = 30;

    /**
     * Processes a command request, awaiting for the response.
     * @param requestCommand
     * @return
     */
    Command processCommand(Command requestCommand, long timeout, TimeUnit timeoutTimeUnit) throws IOException, TimeoutException, InterruptedException;

    /**
     * Composes a command envelope with a get method for the specified resource.
     *
     * @param limeUri
     * @param <TResource>
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    default <TResource extends Document> TResource getResource(final LimeUri limeUri) throws IOException, InterruptedException, TimeoutException {
        return getResource(limeUri, null);
    }

    /**
     * Composes a command envelope with a get method for the specified resource.
     *
     * @param limeUri
     * @param from
     * @param <TResource>
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    default <TResource extends Document> TResource getResource(final LimeUri limeUri, final Node from) throws IOException, InterruptedException, TimeoutException {
        if (limeUri == null) {
            throw new IllegalArgumentException("limeUri");
        }

        final Command requestCommand = new Command(EnvelopeId.newId()) {{
            setMethod(CommandMethod.GET);
            setFrom(from);
            setUri(limeUri);
        }};

        Command responseCommand = processCommand(requestCommand, DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (responseCommand.getStatus() != Command.CommandStatus.SUCCESS) {
            throw new LimeException(responseCommand.getReason());
        }

        return (TResource) responseCommand.getResource();
    }

    /**
     * Composes a command envelope with a set method for the specified resource.
     *
     * @param limeUri
     * @param resource
     * @param <TResource>
     * @throws IOException
     * @throws InterruptedException
     */
    default <TResource extends Document> void setResource(final LimeUri limeUri, final TResource resource) throws IOException, InterruptedException, TimeoutException {
        setResource(limeUri, null, resource);
    }

    /**
     * Composes a command envelope with a set method for the specified resource.
     *
     * @param limeUri
     * @param from
     * @param resource
     * @param <TResource>
     * @throws IOException
     * @throws InterruptedException
     */
    default <TResource extends Document> void setResource(final LimeUri limeUri, final Node from, final TResource resource) throws IOException, InterruptedException, TimeoutException {
        if (limeUri == null) {
            throw new IllegalArgumentException("limeUri");
        }
        if (resource == null) {
            throw new IllegalArgumentException("resource");
        }

        final Command requestCommand = new Command(EnvelopeId.newId()) {{
            setMethod(CommandMethod.SET);
            setResource(resource);
            setFrom(from);
            setUri(limeUri);
        }};

        Command responseCommand = processCommand(requestCommand, DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (responseCommand.getStatus() != Command.CommandStatus.SUCCESS) {
            throw new LimeException(responseCommand.getReason());
        }
    }

    /**
     * Composes a command envelope with a delete method for the specified resource.
     *
     * @param limeUri
     * @throws IOException
     * @throws InterruptedException
     */
    default void deleteResource(final LimeUri limeUri) throws IOException, InterruptedException, TimeoutException {
        deleteResource(limeUri, null);
    }

    /**
     * Composes a command envelope with a delete method for the specified resource.
     *
     * @param limeUri
     * @param from
     * @throws IOException
     * @throws InterruptedException
     */
    default void deleteResource(final LimeUri limeUri, final Node from) throws IOException, InterruptedException, TimeoutException {
        if (limeUri == null) {
            throw new IllegalArgumentException("limeUri");
        }

        final Command requestCommand = new Command(EnvelopeId.newId()) {{
            setMethod(CommandMethod.DELETE);
            setFrom(from);
            setUri(limeUri);
        }};

        Command responseCommand = processCommand(requestCommand, DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (responseCommand.getStatus() != Command.CommandStatus.SUCCESS) {
            throw new LimeException(responseCommand.getReason());
        }
    }
}