package org.limeprotocol.network.modules;

import org.limeprotocol.Command;
import org.limeprotocol.Document;
import org.limeprotocol.JsonDocument;
import org.limeprotocol.MediaType;
import org.limeprotocol.network.CommandChannel;

import java.io.IOException;

/**
 * Defines a channel module that automatically replies to ping request commands.
 */
public class ReplyPingChannelModule extends ChannelModuleBase<Command> {

    private final static String PING_URI_TEMPLATE = "/ping";
    private final static MediaType PING_MEDIA_TYPE = MediaType.parse("application/vnd.lime.ping+json");
    private final static Document PingDocument = new JsonDocument(PING_MEDIA_TYPE);

    private final CommandChannel commandChannel;

    public ReplyPingChannelModule(CommandChannel commandChannel) {
        this.commandChannel = commandChannel;
    }

    @Override
    public Command onReceiving(Command envelope) {
        if (envelope.getId() != null &&
                envelope.getMethod() == Command.CommandMethod.GET &&
                envelope.getStatus() == null &&
                envelope.getUri() != null &&
                envelope.getUri().toString().equalsIgnoreCase(PING_URI_TEMPLATE)) {
            Command pingCommandResponse = new Command(envelope.getId());
            pingCommandResponse.setTo(envelope.getFrom());
            pingCommandResponse.setMethod(Command.CommandMethod.GET);
            pingCommandResponse.setStatus(Command.CommandStatus.SUCCESS);
            pingCommandResponse.setResource(PingDocument);
            try {
                commandChannel.sendCommand(pingCommandResponse);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not send a ping response to the remote node", e);
            }
            return null;
        } else {
            return super.onReceiving(envelope);
        }
    }
}
