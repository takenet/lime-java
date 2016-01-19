package org.limeprotocol.network.modules;

import org.junit.Before;
import org.junit.Test;
import org.limeprotocol.Command;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import org.limeprotocol.Session;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.network.Channel;
import org.limeprotocol.network.Transport;
import org.limeprotocol.testHelpers.Dummy;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.limeprotocol.testHelpers.Dummy.*;
import static org.mockito.Mockito.*;


import java.util.UUID;

import static org.mockito.Mockito.when;

public class ResendMessagesChannelModuleTest {

    @Mock
    private ClientChannel channel;
    @Mock
    private Transport transport;
    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    private int resendMessageTryCount;
    private long resendMessageInterval;
    private long resendMessageIntervalWithSafeMargin;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(transport.isConnected()).thenReturn(true);
        when(channel.getTransport()).thenReturn(transport);
        when(channel.getState()).thenReturn(Session.SessionState.ESTABLISHED);

        resendMessageTryCount = 3;
        resendMessageInterval = 200;
        resendMessageIntervalWithSafeMargin = resendMessageInterval + 50;
    }

    private ResendMessagesChannelModule getTarget() throws IOException {
        final ResendMessagesChannelModule module = ResendMessagesChannelModule.createAndRegister(channel, resendMessageTryCount, resendMessageInterval);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                module.onSending((Message)invocationOnMock.getArguments()[0]);
                return null;
            }
        }).when(channel).sendMessage(any(Message.class));

        return module;
    }

    @Test
    public void onSending_messageWithoutNotification_shouldResendAfterInterval() throws InterruptedException, IOException {
        // Arrange
        Message message = Dummy.createMessage(Dummy.createTextContent());
        message.setId(UUID.randomUUID());
        ResendMessagesChannelModule target = getTarget();

        // Act
        Message actual = (Message)target.onSending(message);
        Thread.sleep(resendMessageIntervalWithSafeMargin);

        // Assert
        assertEquals(message, actual);
        verify(channel, times(1)).sendMessage(message);
    }

    @Test
    public void onSending_messageWithoutNotification_shouldResendUntilLimit() throws InterruptedException, IOException {
        // Arrange
        Message message = Dummy.createMessage(Dummy.createTextContent());
        message.setId(UUID.randomUUID());
        ResendMessagesChannelModule target = getTarget();

        // Act
        Message actual = (Message)target.onSending(message);
        Thread.sleep(resendMessageIntervalWithSafeMargin * (resendMessageTryCount + 1));

        // Assert
        assertEquals(message, actual);
        verify(channel, times(resendMessageTryCount)).sendMessage(message);
    }

    @Test
    public void onSending_receivedNotificationAfterSend_shouldNotResend() throws InterruptedException, IOException {
        // Arrange
        Message message = Dummy.createMessage(Dummy.createTextContent());
        message.setId(UUID.randomUUID());
        Notification notification = Dummy.createNotification(Notification.Event.RECEIVED);
        notification.setId(message.getId());
        notification.setFrom(message.getTo());
        ResendMessagesChannelModule target = getTarget();

        // Act
        Message actualMessage = (Message)target.onSending(message);
        Notification actualNotification = (Notification)target.onReceiving(notification);
        Thread.sleep(resendMessageIntervalWithSafeMargin);

        // Assert
        assertEquals(message, actualMessage);
        assertEquals(notification, actualNotification);
        verify(channel, never()).sendMessage(message);
    }

    @Test
    public void onSending_receivedNotificationAfterFirstResend_shouldNotResend() throws InterruptedException, IOException {
        // Arrange
        Message message = Dummy.createMessage(Dummy.createTextContent());
        message.setId(UUID.randomUUID());
        Notification notification = Dummy.createNotification(Notification.Event.RECEIVED);
        notification.setId(message.getId());
        notification.setFrom(message.getTo());
        ResendMessagesChannelModule target = getTarget();

        // Act
        Message actualMessage = (Message)target.onSending(message);
        Thread.sleep(resendMessageIntervalWithSafeMargin);
        Notification actualNotification = (Notification)target.onReceiving(notification);
        Thread.sleep(resendMessageIntervalWithSafeMargin);

        // Assert
        assertEquals(message, actualMessage);
        assertEquals(notification, actualNotification);
        verify(channel, times(1)).sendMessage(message);
    }

    @Test
    public void onSending_receivedNotificationAfterSecondResend_shouldNotResend() throws InterruptedException, IOException {
        // Arrange
        Message message = Dummy.createMessage(Dummy.createTextContent());
        message.setId(UUID.randomUUID());
        Notification notification = Dummy.createNotification(Notification.Event.RECEIVED);
        notification.setId(message.getId());
        notification.setFrom(message.getTo());
        ResendMessagesChannelModule target = getTarget();

        // Act
        Message actualMessage = (Message)target.onSending(message);
        Thread.sleep(resendMessageIntervalWithSafeMargin * 2);
        Notification actualNotification = (Notification)target.onReceiving(notification);
        Thread.sleep(resendMessageIntervalWithSafeMargin);

        // Assert
        assertEquals(message, actualMessage);
        assertEquals(notification, actualNotification);
        verify(channel, times(2)).sendMessage(message);
    }

    @Test
    public void onStateChanged_establishedToFinishedAfterSend_shouldNotResend() throws InterruptedException, IOException {
        // Arrange
        Message message = Dummy.createMessage(Dummy.createTextContent());
        message.setId(UUID.randomUUID());
        ResendMessagesChannelModule target = getTarget();

        // Act
        Message actual = (Message)target.onSending(message);
        target.onStateChanged(Session.SessionState.FINISHED);
        Thread.sleep(resendMessageIntervalWithSafeMargin);

        // Assert
        assertEquals(message, actual);
        verify(channel, never()).sendMessage(message);
    }

    @Test
    public void onStateChanged_establishedToFinishedAfterSecondResend_shouldNotResendAgain() throws InterruptedException, IOException {
        // Arrange
        Message message = Dummy.createMessage(Dummy.createTextContent());
        message.setId(UUID.randomUUID());
        ResendMessagesChannelModule target = getTarget();

        // Act
        Message actual = (Message)target.onSending(message);
        Thread.sleep(resendMessageIntervalWithSafeMargin * 2);
        target.onStateChanged(Session.SessionState.FINISHED);
        Thread.sleep(resendMessageIntervalWithSafeMargin);

        // Assert
        assertEquals(message, actual);
        verify(channel, times(2)).sendMessage(message);
    }

    @Test
    public void onStateChanged_establishedToFailedAfterSend_shouldNotResend() throws InterruptedException, IOException {
        // Arrange
        Message message = Dummy.createMessage(Dummy.createTextContent());
        message.setId(UUID.randomUUID());
        ResendMessagesChannelModule target = getTarget();

        // Act
        Message actual = (Message)target.onSending(message);
        target.onStateChanged(Session.SessionState.FAILED);
        Thread.sleep(resendMessageIntervalWithSafeMargin);

        // Assert
        assertEquals(message, actual);
        verify(channel, never()).sendMessage(message);
    }

    @Test
    public void onStateChanged_establishedToFailedAfterSecondResend_shouldNotResendAgain() throws InterruptedException, IOException {
        // Arrange
        Message message = Dummy.createMessage(Dummy.createTextContent());
        message.setId(UUID.randomUUID());
        ResendMessagesChannelModule target = getTarget();

        // Act
        Message actual = (Message)target.onSending(message);
        Thread.sleep(resendMessageIntervalWithSafeMargin * 2);
        target.onStateChanged(Session.SessionState.FAILED);
        Thread.sleep(resendMessageIntervalWithSafeMargin);

        // Assert
        assertEquals(message, actual);
        verify(channel, times(2)).sendMessage(message);
    }
}
