package org.limeprotocol.network.modules;

import org.limeprotocol.Envelope;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import org.limeprotocol.Session;
import org.limeprotocol.network.Channel;
import org.limeprotocol.network.ChannelModule;
import sun.util.resources.CalendarData_sr_Latn_BA;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static org.limeprotocol.Session.SessionState.*;

/**
 * Defines a module that resend messages that doesn't have received receipts from the destination.
 */
public final class ResendMessagesChannelModule implements ChannelModule {

    private final static String RESENT_COUNT_KEY = "#resentCount";

    private final int resendMessageTryCount;
    private final long resendMessageInterval;
    private final ConcurrentMap<UUID, SentMessage> sentMessageMap;
    private final BlockingQueue<SentMessage> sentMessageQueue;

    private Channel channel;
    private boolean unbindWhenClosed;
    private Thread consumerThread;

    public ResendMessagesChannelModule(int resendMessageTryCount, long resendMessageInterval) {
        this.resendMessageTryCount = resendMessageTryCount;
        this.resendMessageInterval = resendMessageInterval;
        this.sentMessageMap = new ConcurrentHashMap<>();
        this.sentMessageQueue = new ArrayBlockingQueue<>(100);
    }

    public boolean isBound() {
        return channel != null;
    }

    public synchronized void bind(Channel channel, boolean unbindWhenClosed) {
        if (channel == null) throw new IllegalArgumentException("Invalid channel");
        if (channel.getState() == FINISHED || channel.getState() == FAILED) throw new IllegalArgumentException("The channel has an invalid state");
        if (isBound()) throw new IllegalStateException("The module is already bound to a channel. Call Unbind first.");

        this.channel = channel;
        this.unbindWhenClosed = unbindWhenClosed;
        channel.getMessageModules().add(this);
        channel.getNotificationModules().add(this);
        if (channel.getState() != NEW) {
            onStateChanged(channel.getState());
        }
    }

    public synchronized void unbind() {
        if (!isBound()) throw new IllegalStateException("The module is not bound to a channel");

        channel.getMessageModules().remove(this);
        channel.getNotificationModules().remove(this);
        channel = null;
        if (consumerThread != null && consumerThread.isAlive()) {
            try {
                consumerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void onStateChanged(Session.SessionState state) {
        if (state == ESTABLISHED) {
            consumerThread = new Thread(
                new QueueConsumer()
            );
            consumerThread.start();
        } else if (unbindWhenClosed && (state == FINISHED || state == FAILED)) {
            unbind();
        }
    }

    @Override
    public Envelope onSending(Envelope envelope) {
        if (envelope instanceof Message && envelope.getId() != null) {
            SentMessage sentMessage = sentMessageMap.get(envelope.getId());
            if (sentMessage != null) {
                sentMessage.incrementResentCount();
            } else {
                sentMessage = new SentMessage((Message) envelope);
                sentMessageMap.put(envelope.getId(), sentMessage);
            }

            if (sentMessage.getResentCount() <= resendMessageTryCount) {
                sentMessageQueue.add(sentMessage);
            } else {
                sentMessage = sentMessageMap.remove(envelope.getId());
                if (sentMessage != null) {
                    sentMessage.cancelResent();
                }
            }
        }
        return envelope;
    }

    @Override
    public Envelope onReceiving(Envelope envelope) {
        if (envelope instanceof Notification && envelope.getId() != null) {
            Notification notification = (Notification)envelope;
            if (notification.getEvent() == Notification.Event.RECEIVED || notification.getEvent() == Notification.Event.FAILED) {
                SentMessage sentMessage = sentMessageMap.remove(envelope.getId());
                if (sentMessage != null) {
                    sentMessage.cancelResent();
                }
            }
        }

        return envelope;
    }

    private final class SentMessage {

        private final Message message;
        private final Semaphore semaphore;
        private long lastSentTime;
        private int resentCount;

        private SentMessage(Message message) {
            this.message = message;
            this.lastSentTime = System.currentTimeMillis();
            this.semaphore = new Semaphore(0);
            this.resentCount = 1;
        }

        public Message getMessage() {
            if (message.getMetadata() == null) {
                message.setMetadata(new HashMap<String, String>());
            }
            message.getMetadata().put(RESENT_COUNT_KEY, String.valueOf(resentCount));
            return message;
        }

        public long getLastSentTime() {
            return lastSentTime;
        }

        public int getResentCount() {
            return resentCount;
        }

        public void incrementResentCount() {
            resentCount++;
            this.lastSentTime = System.currentTimeMillis();
        }

        public boolean waitForResent(long resentInterval) throws InterruptedException {
            if (this.semaphore.availablePermits() > 0) {
                return false;
            }

            long now = System.currentTimeMillis();
            long resendTime = lastSentTime + resentInterval;
            if (resendTime > now) {
                long waitInterval = resendTime - now;
                return !this.semaphore.tryAcquire(1, waitInterval, TimeUnit.MILLISECONDS);
            }

            return this.semaphore.availablePermits() == 0;
        }

        public void cancelResent() {
            this.semaphore.release(1);
        }
    }

    private final class QueueConsumer implements Runnable {

        private static final long POLL_INTERVAL = 1000;

        @Override
        public void run() {
            while (isBound()) {
                try {
                    SentMessage sentMessage = sentMessageQueue.poll(POLL_INTERVAL, TimeUnit.MILLISECONDS);
                    if (sentMessage == null) continue;
                    if (sentMessage.waitForResent(resendMessageInterval)) {
                        Channel channel = ResendMessagesChannelModule.this.channel;
                        if (channel == null) {
                            sentMessageQueue.add(sentMessage);
                            continue;
                        }
                        channel.sendMessage(sentMessage.getMessage());
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
