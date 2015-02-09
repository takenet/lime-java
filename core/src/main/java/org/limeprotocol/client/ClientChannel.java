package org.limeprotocol.client;

import org.limeprotocol.Identity;
import org.limeprotocol.Node;
import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;
import org.limeprotocol.network.Channel;
import org.limeprotocol.network.SessionChannel;
import org.limeprotocol.security.Authentication;

import java.util.UUID;

public interface ClientChannel extends Channel {
    
    void startNewSession(SessionChannelListener sessionListener, ChannelListener channelListener);

    void negotiateSession(SessionCompression sessionCompression, SessionEncryption sessionEncryption, SessionChannelListener sessionListener, ChannelListener channelListener);

    void receiveAuthenticationSession(SessionChannelListener sessionListener, ChannelListener channelListener);
    
    void authenticateSession(Identity identity, Authentication authentication, String instance, SessionChannelListener sessionListener, ChannelListener channelListener);
    
    void sendReceivedNotification(UUID messageId, Node to);
    
    void sendFinishingSession();
    
    void receiveFinishedSession(SessionChannelListener sessionListener, ChannelListener channelListener);
}
