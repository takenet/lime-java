package org.limeprotocol.network.modules;

import org.limeprotocol.*;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.network.Channel;
import org.limeprotocol.network.ChannelModule;
import org.limeprotocol.network.SessionChannel;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.limeprotocol.Session.SessionState.ESTABLISHED;


public final class RemotePingChannelModule implements ChannelModule {

    private final static String PING_URI_TEMPLATE = "/ping";

    private final Channel channel;
    private final long pingInterval;
    private final long pingDisconnectionInterval;
    private ScheduledExecutorService executor;
    private ScheduledFuture scheduledPing;
    private long lastReceivedEnvelope;

    private RemotePingChannelModule(Channel channel, long pingInterval, long pingDisconnectionInterval) {
        if (pingInterval < 0) throw new IllegalArgumentException("Invalid ping interval");
        this.channel = channel;
        this.pingInterval = pingInterval;
        this.pingDisconnectionInterval = pingDisconnectionInterval;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void onStateChanged(Session.SessionState state) {
        if (state == ESTABLISHED) {
            setLastReceivedEnvelope(System.currentTimeMillis());
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
        RemotePingChannelModule module = new RemotePingChannelModule(channel, pingInterval, pingDisconnectionInterval);
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
        if (scheduledPing != null) {
            scheduledPing.cancel(false);
            scheduledPing = null;
        }
        if (channel.getState() == ESTABLISHED && channel.getTransport().isConnected()) {
            scheduledPing = executor.schedule(new PingRunnable(), pingInterval, TimeUnit.MILLISECONDS);
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
                    } else {
                        // TODO: Check if is ServerChannel instead of this
                        channel.getTransport().close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
