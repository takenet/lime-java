package org.limeprotocol.network.modules;

import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import org.limeprotocol.network.NotificationChannel;

import java.io.IOException;

/**
 * Defines a channel module that automatically send a received notification for each received message.
 */
public final class NotifyReceiptChannelModule extends ChannelModuleBase<Message> {

    private final NotificationChannel notificationChannel;

    public NotifyReceiptChannelModule(NotificationChannel notificationChannel) {
        this.notificationChannel = notificationChannel;
    }

    @Override
    public Message onReceiving(final Message envelope) {
        if (envelope.getId() != null && envelope.getFrom() != null) {
            try {
                Notification notification = new Notification() {{
                    setId(envelope.getId());
                    setTo(envelope.getSender());
                    setEvent(Event.RECEIVED);
                }};

                notificationChannel.sendNotification(notification);
            } catch (IOException e) {
                throw new RuntimeException("An error occurred while sending a message receipt", e);
            }
        }

        return super.onReceiving(envelope);
    }
}
