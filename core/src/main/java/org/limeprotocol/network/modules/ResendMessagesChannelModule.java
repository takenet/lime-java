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
    private final ConcurrentMap<UUID, CancellationToken> messageCancellationTokenMap;

    private Channel channel;
    private boolean unbindWhenClosed;
    private ScheduledExecutorService executor;
    private List<Runnable> pendingTasks;

    public ResendMessagesChannelModule(int resendMessageTryCount, long resendMessageInterval) {
        this.resendMessageTryCount = resendMessageTryCount;
        this.resendMessageInterval = resendMessageInterval;
        this.messageCancellationTokenMap = new ConcurrentHashMap<>();
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

        this.channel.getMessageModules().remove(this);
        this.channel.getNotificationModules().remove(this);
        this.pendingTasks = executor.shutdownNow();
        this.channel = null;
        this.executor = null;
    }

    @Override
    public synchronized void onStateChanged(Session.SessionState state) {
        if (state == ESTABLISHED) {
            this.executor = Executors.newSingleThreadScheduledExecutor();
            // Reschedule pending tasks, if any
            if (pendingTasks != null) {
                for (Runnable runnable : pendingTasks) {
                    RunnableScheduledFuture scheduledFuture = (RunnableScheduledFuture)runnable;
//
//
//                    long scheduleDelay = resendMessageInterval - (System.currentTimeMillis() - resentMessageRunnable.getLastSentTimeMillis());
//                    if (scheduleDelay < 0) scheduleDelay = 0;
//                    executor.schedule(runnable, scheduleDelay, TimeUnit.MILLISECONDS);
                }
            }
        } else if (unbindWhenClosed && (state == FINISHED || state == FAILED)) {
            unbind();
        }
    }

    @Override
    public Envelope onSending(Envelope envelope) {
        if (envelope instanceof Message && envelope.getId() != null && executor != null) {
            int resentCount = 0;
            if (envelope.getMetadata() != null && envelope.getMetadata().containsKey(RESENT_COUNT_KEY)) {
                resentCount = Integer.parseInt(envelope.getMetadata().get(RESENT_COUNT_KEY));
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

    private class ResentMessageRunnable implements Runnable {

        private final Message message;
        private final int resentCount;

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
                    message.getMetadata().put(RESENT_COUNT_KEY, String.valueOf(resentCount + 1));
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
