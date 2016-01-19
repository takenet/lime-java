package org.limeprotocol.network.modules;

import org.limeprotocol.Envelope;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import org.limeprotocol.Session;
import org.limeprotocol.network.Channel;
import org.limeprotocol.network.ChannelModule;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static org.limeprotocol.Session.SessionState.ESTABLISHED;
import static org.limeprotocol.Session.SessionState.FAILED;
import static org.limeprotocol.Session.SessionState.FINISHED;

/**
 * Defines a module that resend messages that doesn't have received receipts from the destination.
 */
public final class ResendMessagesChannelModule implements ChannelModule {

    private final static String SENT_COUNT_KEY = "#sentCount";

    private final Channel channel;
    private final int resendMessageTryCount;
    private final long resendMessageInterval;
    private final ConcurrentMap<UUID, CancellationToken> messageCancellationTokenMap;
    private final ScheduledExecutorService executor;

    private ResendMessagesChannelModule(Channel channel, int resendMessageTryCount, long resendMessageInterval, ScheduledExecutorService executor) {
        if (channel == null) throw new IllegalArgumentException("channel");
        this.channel = channel;
        this.resendMessageTryCount = resendMessageTryCount;
        this.resendMessageInterval = resendMessageInterval;
        this.messageCancellationTokenMap = new ConcurrentHashMap<>();
        this.executor = executor;
    }

    @Override
    public synchronized void onStateChanged(Session.SessionState state) {
        if (state == FINISHED || state == FAILED) {
            for (CancellationToken cancellationToken :  messageCancellationTokenMap.values()) {
                cancellationToken.cancel(false);
            }
            messageCancellationTokenMap.clear();
        }
    }

    @Override
    public Envelope onSending(Envelope envelope) {
        if (envelope instanceof Message && envelope.getId() != null) {
            int resentCount = 0;
            if (envelope.getMetadata() != null && envelope.getMetadata().containsKey(SENT_COUNT_KEY)) {
                resentCount = Integer.parseInt(envelope.getMetadata().get(SENT_COUNT_KEY));
            }

            if (resentCount < resendMessageTryCount) {
                CancellationToken cancellationToken;
                if (resentCount > 0) {
                    cancellationToken = messageCancellationTokenMap.get(envelope.getId());
                } else {
                    cancellationToken = new CancellationToken();
                    messageCancellationTokenMap.put(envelope.getId(), cancellationToken);
                }

                if (cancellationToken != null) {
                    Message message = (Message) envelope;
                    ResentMessageRunnable runnable = new ResentMessageRunnable(message, resentCount);
                    ScheduledFuture scheduledFuture = executor.schedule(runnable, resendMessageInterval, TimeUnit.MILLISECONDS);
                    cancellationToken.addFuture(scheduledFuture);
                }
            } else {
                messageCancellationTokenMap.remove(envelope.getId());
            }
        }

        return envelope;
    }

    @Override
    public Envelope onReceiving(Envelope envelope) {
        if (envelope instanceof Notification) {
            Notification notification = (Notification)envelope;
            if (notification.getEvent() == Notification.Event.RECEIVED || notification.getEvent() == Notification.Event.FAILED) {
                CancellationToken cancellationToken = messageCancellationTokenMap.get(envelope.getId());
                if (cancellationToken != null) {
                    cancellationToken.cancel(false);
                }
            }
        }

        return envelope;
    }
    public static ResendMessagesChannelModule createAndRegister(Channel channel, int resendMessageTryCount, long resendMessageInterval) {
        return createAndRegister(channel, resendMessageTryCount, resendMessageInterval, Executors.newSingleThreadScheduledExecutor());
    }

    public static ResendMessagesChannelModule createAndRegister(Channel channel, int resendMessageTryCount, long resendMessageInterval, ScheduledExecutorService executor) {
        ResendMessagesChannelModule module = new ResendMessagesChannelModule(channel, resendMessageTryCount, resendMessageInterval, executor);
        channel.getMessageModules().add(module);
        channel.getNotificationModules().add(module);
        return module;
    }

    private class ResentMessageRunnable implements Runnable {

        private final Message message;
        private int resentCount;

        public ResentMessageRunnable(Message message, int resentCount) {
            this.message = message;
            this.resentCount = resentCount;
        }

        @Override
        public void run() {
            if (channel.getState() == ESTABLISHED && channel.getTransport().isConnected()) {
                try {
                    if (message.getMetadata() == null) {
                        message.setMetadata(new HashMap<String, String>());
                    }
                    resentCount++;
                    message.getMetadata().put(SENT_COUNT_KEY, String.valueOf(resentCount));
                    channel.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private final class CancellationToken {
        private final List<ScheduledFuture> futures;

        private CancellationToken() {
            futures = new ArrayList<>();
        }

        public void addFuture(ScheduledFuture future) {
            futures.add(future);
            removeComplete();
        }

        private void removeComplete() {
            Iterator<ScheduledFuture> iterator = futures.iterator();
            while (iterator.hasNext()) {
                ScheduledFuture future = iterator.next();
                if (future.isDone() || future.isCancelled()) {
                    iterator.remove();
                }
            }
        }

        public void cancel(boolean mayInterruptIfRunning) {
            for (ScheduledFuture future : futures) {
                if (!future.isDone() && !future.isCancelled()) {
                    future.cancel(mayInterruptIfRunning);
                }
            }
            futures.clear();
        }
    }
}
