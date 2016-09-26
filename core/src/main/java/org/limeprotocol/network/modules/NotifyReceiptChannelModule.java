package org.limeprotocol.network.modules;

import org.limeprotocol.Message;
import org.limeprotocol.Node;
import org.limeprotocol.Notification;
import org.limeprotocol.network.Channel;

import java.io.IOException;

/**
 * Defines a channel module that automatically send a received notification for each received message.
 */
public final class NotifyReceiptChannelModule extends ChannelModuleBase<Message> {

    private final Channel channel;

    public NotifyReceiptChannelModule(Channel channel) {
        this.channel = channel;
    }

    @Override
    public Message onReceiving(final Message envelope) {
        Node destination = envelope.getTo();

        if (envelope.getId() != null &&
                envelope.getFrom() != null &&
                (destination == null || destination.equals(channel.getLocalNode()) || (destination.getInstance() == null && destination.toIdentity().equals(channel.getLocalNode().toIdentity())))) {
            try {
                Notification notification = new Notification() {{
                    setId(envelope.getId());
                    setTo(envelope.getSender());
                    setEvent(Event.RECEIVED);
                }};

                channel.sendNotification(notification);
            } catch (IOException e) {
                throw new RuntimeException("An error occurred while sending a message receipt", e);
            }
        }

        return super.onReceiving(envelope);
    }
}
