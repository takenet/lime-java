package org.limeprotocol.network;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ChannelBaseTest {

    private Transport transport;

    private ChannelBase getTarget() {
        return getTarget(false);
    }
    
    private ChannelBase getTarget(boolean fillEnvelopeRecipients) {
        return new TestChannel(mock(Transport.class), fillEnvelopeRecipients);
    }
    
    private class TestChannel extends ChannelBase {
        protected TestChannel(Transport transport, boolean fillEnvelopeRecipients) {
            super(transport, fillEnvelopeRecipients);
        }
    }
    
}