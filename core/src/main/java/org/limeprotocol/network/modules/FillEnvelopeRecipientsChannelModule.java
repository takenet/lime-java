package org.limeprotocol.network.modules;

import org.limeprotocol.*;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.network.Channel;
import org.limeprotocol.util.StringUtils;

/**
 * Defines a channel module that fills envelope receipts based on the channel information.
 * @param <T>
 */
public final class FillEnvelopeRecipientsChannelModule<T extends Envelope> extends ChannelModuleBase<T> {

    private final Channel channel;

    public FillEnvelopeRecipientsChannelModule(Channel channel) {
        this.channel = channel;
    }

    @Override
    public T onSending(T envelope) {
        if (channel instanceof ClientChannel && channel.getLocalNode() != null) {
            if (envelope.getPp() == null) {
                if (envelope.getFrom() != null && !envelope.getFrom().equals(channel.getLocalNode())) {
                    envelope.setPp(channel.getLocalNode().copy());
                }
            } else if (StringUtils.isNullOrWhiteSpace(envelope.getPp().getDomain())) {
                envelope.getPp().setDomain(channel.getLocalNode().getDomain());
            }
        }

        return super.onSending(envelope);
    }

    @Override
    public T onReceiving(T envelope) {
        Node from = channel.getRemoteNode();
        Node to = channel.getLocalNode();

        if (from != null) {
            if (envelope.getFrom() == null) {
                envelope.setFrom(from.copy());
            } else if (StringUtils.isNullOrEmpty(envelope.getFrom().getDomain())) {
                envelope.getFrom().setDomain(from.getDomain());
            }
        }

        if (to != null) {
            if (envelope.getTo() == null) {
                envelope.setTo(to.copy());
            } else if (StringUtils.isNullOrEmpty(envelope.getTo().getDomain())) {
                envelope.getTo().setDomain(to.getDomain());
            }
        }

        return super.onReceiving(envelope);
    }

    public static void createAndRegister(Channel channel) {
        FillEnvelopeRecipientsChannelModule<Message> messageModule = new FillEnvelopeRecipientsChannelModule<>(channel);
        FillEnvelopeRecipientsChannelModule<Notification> notificationModule = new FillEnvelopeRecipientsChannelModule<>(channel);
        FillEnvelopeRecipientsChannelModule<Command> commandModule = new FillEnvelopeRecipientsChannelModule<>(channel);
        channel.getMessageModules().add(messageModule);
        channel.getNotificationModules().add(notificationModule);
        channel.getCommandModules().add(commandModule);
    }
}
