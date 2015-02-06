package org.limeprotocol.network;

import org.limeprotocol.Notification;

import java.io.IOException;

/**
 * Defines a notification envelopes exchanging channel.
 */
public interface NotificationChannel {
    /**
     * Sends a notification to the remote node.
     * @param notification
     */
    void sendNotification(Notification notification) throws IOException;

    /**
     * Sets the listener for receiving notifications.
     * @param notificationChannelListener
     */
    void setNotificationChannelListener(NotificationChannelListener notificationChannelListener);

    /**
     * Defines a notification channel listener.
     */
    public interface NotificationChannelListener {
        /**
         * Occurs when a notification is received by the channel.
         * @param notification
         */
        void onReceiveNotification(Notification notification);
    }
}
