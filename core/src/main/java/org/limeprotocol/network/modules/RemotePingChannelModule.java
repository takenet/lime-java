package org.limeprotocol.network.modules;

import org.limeprotocol.*;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.network.Channel;
import org.limeprotocol.network.ChannelModule;

import java.util.UUID;
import java.util.concurrent.*;

import static org.limeprotocol.Session.SessionState.ESTABLISHED;
import static org.limeprotocol.Session.SessionState.FAILED;
import static org.limeprotocol.Session.SessionState.FINISHED;

/**
 * Defines a module that pings the remote party after a period of inactivity.
 */
public final class RemotePingChannelModule implements ChannelModule {

    private final static String PING_URI_TEMPLATE = "/ping";

    private final Channel channel;
    private final long pingInterval;
    private final long pingDisconnectionInterval;
    private final PingRunnable pingRunnable;
    private final ScheduledExecutorService executor;
    private ScheduledFuture scheduledPing;
    private long lastReceivedEnvelope;


    private RemotePingChannelModule(Channel channel, long pingInterval, long pingDisconnectionInterval, ScheduledExecutorService executor) {
        if (pingInterval < 0) throw new IllegalArgumentException("Invalid ping interval");
        this.channel = channel;
        this.pingInterval = pingInterval;
        this.pingDisconnectionInterval = pingDisconnectionInterval;
        this.executor =  executor;
        this.pingRunnable = new PingRunnable();
    }

    @Override
    public synchronized void onStateChanged(Session.SessionState state) {
        if (state == ESTABLISHED) {
            setLastReceivedEnvelope(System.currentTimeMillis());
        } else if (state == FINISHED || state == FAILED) {
            cancelScheduledPing();
        }
    }

    @Override
    public Envelope onSending(Envelope envelope) {
        return envelope;
    }

    @Override
    public Envelope onReceiving(Envelope envelope) {
        setLastReceivedEnvelope(System.currentTimeMillis());
        return envelope;
    }

    public static RemotePingChannelModule createAndRegister(Channel channel, long pingInterval, long pingDisconnectionInterval) {
        return createAndRegister(channel, pingInterval, pingDisconnectionInterval, Executors.newSingleThreadScheduledExecutor());
    }

    public static RemotePingChannelModule createAndRegister(Channel channel, long pingInterval, long pingDisconnectionInterval, ScheduledExecutorService executor) {
        RemotePingChannelModule module = new RemotePingChannelModule(channel, pingInterval, pingDisconnectionInterval, executor);
        channel.getMessageModules().add(module);
        channel.getNotificationModules().add(module);
        channel.getCommandModules().add(module);
        return module;
    }

    private void setLastReceivedEnvelope(long time) {
        this.lastReceivedEnvelope = time;
        schedulePing();
    }

    private synchronized void schedulePing() {
        cancelScheduledPing();
        if (channel.getState() == ESTABLISHED && channel.getTransport().isConnected()) {
            scheduledPing = executor.schedule(pingRunnable, pingInterval, TimeUnit.MILLISECONDS);
        }
    }

    private synchronized void cancelScheduledPing() {
        if (scheduledPing != null) {
            if (!scheduledPing.isCancelled() && !scheduledPing.isDone()) {
                scheduledPing.cancel(false);
            }
            scheduledPing = null;
        }
    }

    private class PingRunnable implements Runnable {
        @Override
        public void run() {
            if (channel.getState() == ESTABLISHED && channel.getTransport().isConnected()) {
                try {
                    if (pingDisconnectionInterval == 0 || System.currentTimeMillis() - lastReceivedEnvelope < pingDisconnectionInterval) {
                        Command pingCommand = new Command(UUID.randomUUID());
                        pingCommand.setMethod(Command.CommandMethod.GET);
                        pingCommand.setUri(new LimeUri(PING_URI_TEMPLATE));
                        channel.sendCommand(pingCommand);

                        schedulePing();
                    } else if (channel instanceof ClientChannel) {
                        ((ClientChannel) channel).sendFinishingSession();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
