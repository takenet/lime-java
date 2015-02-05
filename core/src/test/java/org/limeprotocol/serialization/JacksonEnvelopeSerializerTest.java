package org.limeprotocol.serialization;

import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;

import org.junit.Before;
import org.junit.Test;
import org.limeprotocol.Command;
import org.limeprotocol.Envelope;
import org.limeprotocol.JsonDocument;
import org.limeprotocol.Message;
import org.limeprotocol.Node;
import org.limeprotocol.Notification;
import org.limeprotocol.PlainDocument;
import org.limeprotocol.Session;
import org.limeprotocol.Session.SessionState;
import org.limeprotocol.security.GuestAuthentication;
import org.limeprotocol.security.PlainAuthentication;
import org.limeprotocol.testHelpers.JsonConstants;
import org.limeprotocol.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.limeprotocol.security.Authentication.AuthenticationScheme;
import static org.limeprotocol.testHelpers.JsonConstants.Command.METHOD_KEY;
import static org.limeprotocol.testHelpers.JsonConstants.Command.STATUS_KEY;
import static org.limeprotocol.testHelpers.JsonConstants.Command.URI_KEY;
import static org.limeprotocol.testHelpers.JsonConstants.Envelope.FROM_KEY;
import static org.limeprotocol.testHelpers.JsonConstants.Envelope.ID_KEY;
import static org.limeprotocol.testHelpers.JsonConstants.Envelope.METADATA_KEY;
import static org.limeprotocol.testHelpers.JsonConstants.Envelope.PP_KEY;
import static org.limeprotocol.testHelpers.JsonConstants.Envelope.TO_KEY;
import static org.limeprotocol.testHelpers.JsonConstants.Envelope.getMetadataKeyFromRoot;
import static org.limeprotocol.testHelpers.JsonConstants.Notification.CODE_FROM_REASON_KEY;
import static org.limeprotocol.testHelpers.JsonConstants.Notification.DESCRIPTION_FROM_REASON_KEY;
import static org.limeprotocol.testHelpers.JsonConstants.Notification.EVENT_KEY;
import static org.limeprotocol.testHelpers.JsonConstants.Session.AUTHENTICATION_KEY;
import static org.limeprotocol.testHelpers.JsonConstants.Session.STATE_KEY;
import static org.limeprotocol.testHelpers.TestDummy.createAbsoluteLimeUri;
import static org.limeprotocol.testHelpers.TestDummy.createCommand;
import static org.limeprotocol.testHelpers.TestDummy.createJsonDocument;
import static org.limeprotocol.testHelpers.TestDummy.createMessage;
import static org.limeprotocol.testHelpers.TestDummy.createNode;
import static org.limeprotocol.testHelpers.TestDummy.createNotification;
import static org.limeprotocol.testHelpers.TestDummy.createPlainAuthentication;
import static org.limeprotocol.testHelpers.TestDummy.createPlainDocument;
import static org.limeprotocol.testHelpers.TestDummy.createRandomMetadata;
import static org.limeprotocol.testHelpers.TestDummy.createRandomString;
import static org.limeprotocol.testHelpers.TestDummy.createReason;
import static org.limeprotocol.testHelpers.TestDummy.createSession;


public class JacksonEnvelopeSerializerTest {

    private JacksonEnvelopeSerializer target;

    @Before
    public void setUp() throws Exception {
        target = new JacksonEnvelopeSerializer();
    }

    //region serialize method

    //region Session

    @Test
    public void serialize_AuthenticatingSession_ReturnsValidJsonString() {
        // Arrange
        Session session = createSession(SessionState.Authenticating);
        PlainAuthentication plainAuthentication = createPlainAuthentication();
        session.setAuthentication(plainAuthentication);

        Map<String, String> metadata = createRandomMetadata();
        session.setMetadata(metadata);

        // Act
        String resultString = target.serialize(session);

        // Assert
        assertJsonEnvelopeProperties(session, resultString, ID_KEY, FROM_KEY, TO_KEY, METADATA_KEY);

        assertThatJson(resultString).node(STATE_KEY).isEqualTo(session.getState().toString().toLowerCase());
        assertThatJson(resultString).node(AUTHENTICATION_KEY).isPresent();
        assertThatJson(resultString).node(JsonConstants.PlainAuthentication.PASSWORK_KEY_FROM_ROOT).isEqualTo(plainAuthentication.getPassword());

        assertThatJson(resultString).node(JsonConstants.Command.REASON_KEY).isAbsent();
    }

    @Test
    public void serialize_FailedSession_ReturnsValidJsonString() {
        // Arrange
        Session session = createSession();
        session.setState(SessionState.Failed);
        session.setReason(createReason());

        // Act
        String resultString = target.serialize(session);

        // Assert
        assertJsonEnvelopeProperties(session, resultString, ID_KEY, FROM_KEY, TO_KEY);
        assertThatJson(resultString).node(STATE_KEY).isEqualTo(session.getState().toString().toLowerCase());
        assertThatJson(resultString).node(JsonConstants.Session.REASON_KEY).isPresent();
        assertThatJson(resultString).node(JsonConstants.Reason.CODE_KEY_FROM_ROOT).isEqualTo(session.getReason().getCode());
        assertThatJson(resultString).node(JsonConstants.Reason.DESCRIPTION_KEY_FROM_ROOT).isEqualTo(session.getReason().getDescription());

        assertThatJson(resultString).node(AUTHENTICATION_KEY).isAbsent();
    }

    //endregion Session

    //region Command

    @Test
    public void serialize_AbsoluteUriRequestCommand_ReturnsValidJsonString() {
        // Arrange
        Command command = createCommand();
        command.setPp(createNode());
        command.setUri(createAbsoluteLimeUri());

        Map<String, String> metadata = createRandomMetadata();
        command.setMetadata(metadata);

        // Act
        String resultString = target.serialize(command);

        // Assert
        assertJsonEnvelopeProperties(command, resultString, ID_KEY, FROM_KEY, TO_KEY, PP_KEY, METADATA_KEY);

        assertThatJson(resultString).node(METHOD_KEY).isEqualTo(command.getMethod().toString().toLowerCase());
        assertThatJson(resultString).node(URI_KEY).isEqualTo(command.getUri().toString());

        assertThatJson(resultString).node(STATUS_KEY).isAbsent();
        assertThatJson(resultString).node(JsonConstants.Command.REASON_KEY).isAbsent();
        assertThatJson(resultString).node(JsonConstants.Command.TYPE_KEY).isAbsent();
        assertThatJson(resultString).node(JsonConstants.Command.REASON_KEY).isAbsent();
    }

    //endregion Command

    //region Message


    @Test
    public void serialize_UnknownJsonContentMessage_ReturnsValidJsonString()
    {
        JsonDocument content = createJsonDocument();
        Message message = createMessage(content);
        message.setPp(createNode());

        message.setMetadata(createRandomMetadata());

        String resultString = target.serialize(message);

        assertJsonEnvelopeProperties(message, resultString, ID_KEY, FROM_KEY, TO_KEY, PP_KEY, METADATA_KEY);

        assertThatJson(resultString).node(JsonConstants.Message.TYPE_KEY).isEqualTo(message.getType().toString());
        assertThatJson(resultString).node(JsonConstants.Message.CONTENT_KEY).isPresent();

        for (Map.Entry<String, Object> keyValuePair : content.entrySet())
        {
            assertThatJson(resultString).node(keyValuePair.getKey()).isEqualTo(keyValuePair.getValue());
        }
    }

    @Test
    public void serialize_UnknownPlainContentMessage_ReturnsValidJsonString()
    {
        PlainDocument content = createPlainDocument();
        Message message = createMessage(content);
        message.setPp(createNode());

        message.setMetadata(createRandomMetadata());

        String resultString = target.serialize(message);

        assertJsonEnvelopeProperties(message, resultString, ID_KEY, FROM_KEY, TO_KEY, PP_KEY, METADATA_KEY);

        assertThatJson(resultString).node(JsonConstants.Message.TYPE_KEY).isEqualTo(message.getType().toString());
        assertThatJson(resultString).node(JsonConstants.Message.CONTENT_KEY).isPresent();

        assertThatJson(resultString).node(JsonConstants.Message.CONTENT_KEY).isEqualTo(content.getValue());
    }
//
//    public void Serialize_UnknownJsonContentMessage_ReturnsValidJsonString()
//    {
//        var target = GetTarget();
//
//        var content = DataUtil.CreateJsonDocument();
//        var message = DataUtil.CreateMessage(content);
//        message.Pp = DataUtil.CreateNode();
//
//        var metadataKey1 = "randomString1";
//        var metadataValue1 = DataUtil.CreateRandomString(50);
//        var metadataKey2 = "randomString2";
//        var metadataValue2 = DataUtil.CreateRandomString(50);
//        message.Metadata = new Dictionary<string, string>();
//        message.Metadata.Add(metadataKey1, metadataValue1);
//        message.Metadata.Add(metadataKey2, metadataValue2);
//
//        var resultString = target.Serialize(message);
//
//        Assert.IsTrue(resultString.HasValidJsonStackedBrackets());
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.ID_KEY, message.Id));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.FROM_KEY, message.From));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.PP_KEY, message.Pp));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.TO_KEY, message.To));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Message.TYPE_KEY, message.Content.GetMediaType()));
//        Assert.IsTrue(resultString.ContainsJsonKey(Message.CONTENT_KEY));
//
//        foreach (var keyValuePair in content)
//        {
//            Assert.IsTrue(resultString.ContainsJsonProperty(keyValuePair.Key, keyValuePair.Value));
//        }
//
//        Assert.IsTrue(resultString.ContainsJsonProperty(metadataKey1, metadataValue1));
//        Assert.IsTrue(resultString.ContainsJsonProperty(metadataKey2, metadataValue2));
//    }
//
//    public void Serialize_UnknownPlainContentMessage_ReturnsValidJsonString()
//    {
//        var target = GetTarget();
//
//        var content = DataUtil.CreatePlainDocument();
//        var message = DataUtil.CreateMessage(content);
//        message.Pp = DataUtil.CreateNode();
//
//        var metadataKey1 = "randomString1";
//        var metadataValue1 = DataUtil.CreateRandomString(50);
//        var metadataKey2 = "randomString2";
//        var metadataValue2 = DataUtil.CreateRandomString(50);
//        message.Metadata = new Dictionary<string, string>();
//        message.Metadata.Add(metadataKey1, metadataValue1);
//        message.Metadata.Add(metadataKey2, metadataValue2);
//
//        var resultString = target.Serialize(message);
//
//        Assert.IsTrue(resultString.HasValidJsonStackedBrackets());
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.ID_KEY, message.Id));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.FROM_KEY, message.From));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.PP_KEY, message.Pp));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.TO_KEY, message.To));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Message.TYPE_KEY, message.Content.GetMediaType()));
//        Assert.IsTrue(resultString.ContainsJsonKey(Message.CONTENT_KEY));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Message.CONTENT_KEY, content.Value));
//        Assert.IsTrue(resultString.ContainsJsonProperty(metadataKey1, metadataValue1));
//        Assert.IsTrue(resultString.ContainsJsonProperty(metadataKey2, metadataValue2));
//    }
//
//    public void Serialize_FireAndForgetTextMessage_ReturnsValidJsonString()
//    {
//        var target = GetTarget();
//
//        var content = DataUtil.CreateTextContent();
//        var message = DataUtil.CreateMessage(content);
//        message.Id = Guid.Empty;
//
//        var resultString = target.Serialize(message);
//
//        Assert.IsTrue(resultString.HasValidJsonStackedBrackets());
//
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.FROM_KEY, message.From));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.TO_KEY, message.To));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Message.TYPE_KEY, message.Content.GetMediaType()));
//        Assert.IsTrue(resultString.ContainsJsonKey(Message.CONTENT_KEY));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Message.CONTENT_KEY, content.Text));
//
//        Assert.IsFalse(resultString.ContainsJsonKey(Envelope.ID_KEY));
//        Assert.IsFalse(resultString.ContainsJsonKey(Envelope.PP_KEY));
//        Assert.IsFalse(resultString.ContainsJsonKey(Envelope.METADATA_KEY));
//    }

    //endregion Message

    //region Notification

    @Test
    public void Serialize_FailedNotification_ReturnsValidJsonString()
    {
        Notification notification = createNotification(Notification.Event.Failed);
        notification.setId(UUID.randomUUID());
        notification.setReason(createReason());

        String resultString = target.serialize(notification);

        assertJsonEnvelopeProperties(notification, resultString, ID_KEY, FROM_KEY, TO_KEY);

        assertThatJson(resultString).node(EVENT_KEY).isEqualTo(notification.event.toString().toLowerCase());
        assertThatJson(resultString).node(CODE_FROM_REASON_KEY).isEqualTo(notification.getReason().getCode());
        assertThatJson(resultString).node(DESCRIPTION_FROM_REASON_KEY).isEqualTo(notification.getReason().getDescription());

        assertThatJson(resultString).node(PP_KEY).isAbsent();
        assertThatJson(resultString).node(METADATA_KEY).isAbsent();
    }

    @Test
    public void serialize_ReceivedNotification_ReturnsValidJsonString()
    {
        Notification notification = createNotification(Notification.Event.Received);
        notification.setId(UUID.randomUUID());
        notification.setPp(createNode());

        String metadataKey1 = "randomString1";
        String metadataValue1 = createRandomString(50);
        String metadataKey2 = "randomString2";
        String metadataValue2 = createRandomString(50);

        Map <String, String> metadata = new HashMap<>();
        metadata.put(metadataKey1, metadataValue1);
        metadata.put(metadataKey2, metadataValue2);
        notification.setMetadata(metadata);

        String resultString = target.serialize(notification);

        assertJsonEnvelopeProperties(notification, resultString, ID_KEY, FROM_KEY, TO_KEY, PP_KEY, METADATA_KEY);

        assertThatJson(resultString).node(EVENT_KEY).isEqualTo(notification.event.toString().toLowerCase());

        assertThatJson(resultString).node(JsonConstants.Notification.REASON_KEY).isAbsent();
    }

    //endregion Notification

    //endregion serialize

    //region deserialize method

    @Test
    public void deserialize_AuthenticatingSession_ReturnsValidInstance() {
        // Arrange
        UUID id = UUID.randomUUID();
        Node from = createNode();
        Node to = createNode();

        String password = StringUtils.toBase64(createRandomString(10));

        String randomKey1 = "randomString1";
        String randomKey2 = "randomString2";
        String randomString1 = createRandomString(50);
        String randomString2 = createRandomString(50);

        SessionState state = SessionState.Authenticating;

        AuthenticationScheme scheme = AuthenticationScheme.Plain;

        String json = StringUtils.format("{\"state\":\"{0}\",\"scheme\":\"{9}\",\"authentication\":{\"password\":\"{1}\"},\"id\":\"{2}\",\"from\":\"{3}\",\"to\":\"{4}\",\"metadata\":{\"{5}\":\"{6}\",\"{7}\":\"{8}\"}}",
                state.toString().toLowerCase(),
                password,
                id,
                from,
                to,
                randomKey1,
                randomString1,
                randomKey2,
                randomString2,
                scheme.toString().toLowerCase()
        );
        // Act
        Envelope envelope = target.deserialize(json);

        // Assert
        assertThat(envelope).isInstanceOf(Session.class);

        Session session = (Session)envelope;
        assertThat(session.getId()).isEqualTo(id);
        assertThat(session.getFrom()).isEqualTo(from);
        assertThat(session.getTo()).isEqualTo(to);
        assertThat(session.getMetadata()).isNotNull();
        assertThat(session.getMetadata()).containsKey(randomKey1);
        assertThat(session.getMetadata().get(randomKey1)).isEqualTo(randomString1);
        assertThat(session.getMetadata()).containsKey(randomKey2);
        assertThat(session.getMetadata().get(randomKey2)).isEqualTo(randomString2);

        assertThat(session.getState()).isEqualTo(state);

        assertThat(session.getPp()).isNull();
        assertThat(session.getReason()).isNull();

        assertThat(session.getScheme()).isEqualTo(AuthenticationScheme.Plain);
    }

    @Test
    public void deserialize_FailedSessionNullProperties_ReturnsValidInstance(){
        // Arrange
        UUID id = UUID.randomUUID();
        Node from = createNode();
        Node to = createNode();

        SessionState state = SessionState.Authenticating;

        int reasonCode = 57;
        String reasonDescription = "Unit test";

        String json = StringUtils.format(
                "{\"state\":\"{0}\",\"id\":\"{1}\",\"from\":\"{2}\",\"to\":\"{3}\",\"reason\":{\"code\":{4},\"description\":\"{5}\"}},\"encryptionOptions\":null,\"compressionOptions\":null,\"compression\":null,\"encryption\":null}}",
                state.toString().toLowerCase(),
                id,
                from,
                to,
                reasonCode,
                reasonDescription
        );


        // ACT
        Envelope envelope = target.deserialize(json);

        // ASSERT
        assertTrue(envelope instanceof Session);

        Session session = (Session) envelope;

        assertEquals(id, session.getId());
        assertEquals(from, session.getFrom());
        assertEquals(to, session.getTo());
        assertEquals(state, session.getState());
        assertNotNull(session.getReason());
        assertEquals(reasonCode, session.getReason().getCode());
        assertNull(session.getPp());
        assertNull(session.getMetadata());

    }

    @Test
    public void deserialize_SessionAuthenticatingWithPlainAuthentication_ReturnsValidInstance()
    {
        // Arrange

        String json = "{\"state\":\"authenticating\",\"scheme\":\"plain\",\"authentication\":{\"password\":\"Zg==\"},\"id\":\"ec9c196c-da09-43b0-923b-8ec162705c32\",\"from\":\"andre@takenet.com.br/MINELLI-NOTE\"}";

        // Act
        Envelope envelope = target.deserialize(json);

        // Assert

        assertTrue(envelope instanceof Session);
        Session session = (Session)envelope;
        assertEquals(session.getScheme(), AuthenticationScheme.Plain);
        assertTrue(session.getAuthentication() instanceof PlainAuthentication );
        PlainAuthentication authentication = (PlainAuthentication)session.getAuthentication();
        assertFalse(StringUtils.isNullOrEmpty(authentication.getPassword()));

    }

    @Test
    public void deserialize_SessionAuthenticatingWithGuestAuthentication_ReturnsValidInstance()
    {
        // Arrange

        String json = "{\"state\":\"authenticating\",\"scheme\":\"guest\",\"id\":\"feeb88e2-c209-40cd-b8ab-e14aeebe57ab\",\"from\":\"ca6829ff-1ac8-4dad-ad78-c25a3e4f8f7b@takenet.com.br/MINELLI-NOTE\"}";

        // Act
        Envelope envelope = target.deserialize(json);

        // Assert

        assertTrue(envelope instanceof Session);

        Session session = (Session)envelope;
        assertTrue(session.getScheme().equals(AuthenticationScheme.Guest));
        assertTrue(session.getAuthentication() instanceof GuestAuthentication);
    }

    //endregion Session

    //endregion deserialize

    private void assertJsonEnvelopeProperties(Envelope expected, String jsonString, String... properties) {
        List<String> missingKeys = new ArrayList<>(Arrays.asList(JsonConstants.Envelope.ALL_KEYS));

        for(String property : properties) {
            JsonFluentAssert jsonFluentAssert = assertThatJson(jsonString).node(property);

            missingKeys.remove(property);

            switch (property) {
                case ID_KEY:
                    jsonFluentAssert.isEqualTo(expected.getId());
                    break;
                case FROM_KEY:
                    jsonFluentAssert.isEqualTo(expected.getFrom().toString());
                    break;
                case TO_KEY:
                    jsonFluentAssert.isEqualTo(expected.getTo().toString());
                    break;
                case PP_KEY:
                    jsonFluentAssert.isEqualTo(expected.getPp().toString());
                    break;
                case METADATA_KEY:
                    for (String key : expected.getMetadata().keySet()) {
                        assertThatJson(jsonString).node(getMetadataKeyFromRoot(key)).isEqualTo(expected.getMetadata().get(key));
                    }
                    break;
            }
        }

        for(String missingKey : missingKeys) {
            assertThatJson(jsonString).node(missingKey).isAbsent();
        }
    }

}