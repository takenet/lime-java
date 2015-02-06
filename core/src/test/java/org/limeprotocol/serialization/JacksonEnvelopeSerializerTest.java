package org.limeprotocol.serialization;

import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;

import org.junit.Before;
import org.junit.Test;
import org.limeprotocol.*;
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
import static org.junit.Assert.*;
import static org.limeprotocol.Command.*;
import static org.limeprotocol.Command.CommandMethod.*;
import static org.limeprotocol.security.Authentication.AuthenticationScheme;
import static org.limeprotocol.testHelpers.JsonConstants.Command.*;
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
import static org.limeprotocol.testHelpers.TestDummy.*;


public class JacksonEnvelopeSerializerTest {

    private JacksonEnvelopeSerializer target;

    @Before
    public void setUp() throws Exception {
        target = new JacksonEnvelopeSerializer();
    }

    //region serialize method

    //region Session

    @Test
    //TODO: Instability
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
    }

    @Test
    public void serialize_RelativeUriRequestCommand_ReturnsValidJsonString() {
        // Arrange
        JsonDocument resource = createJsonDocument();

        Command command = createCommand(resource);
        command.setPp(createNode());
        command.setMethod(Set);
        command.setUri(createRelativeLimeUri());

        // Act
        String resultString = target.serialize(command);

        // Assert
        assertJsonEnvelopeProperties(command, resultString, ID_KEY, FROM_KEY, TO_KEY, PP_KEY);

        assertThatJson(resultString).node(METHOD_KEY).isEqualTo(command.getMethod().toString().toLowerCase());
        assertThatJson(resultString).node(URI_KEY).isEqualTo(command.getUri().toString());
        assertThatJson(resultString).node(JsonConstants.Command.TYPE_KEY).isEqualTo(command.getType().toString());
        assertThatJson(resultString).node(RESOURCE_KEY).isPresent();

        assertThatJson(resultString).node(STATUS_KEY).isAbsent();
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
            assertThatJson(resultString).node(JsonConstants.Message.CONTENT_KEY + "." + keyValuePair.getKey()).isPresent();
            assertThatJson(resultString).node(JsonConstants.Message.CONTENT_KEY  + "." + keyValuePair.getKey()).isEqualTo(keyValuePair.getValue().toString());
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

        assertThatJson(resultString).node(JsonConstants.Message.CONTENT_VALUE_KEY).isEqualTo(content.getValue());
    }

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

        assertThatJson(resultString).node(EVENT_KEY).isEqualTo(notification.getEvent().toString().toLowerCase());
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

        notification.setMetadata(createRandomMetadata("randomString1", "randomString2"));

        String resultString = target.serialize(notification);

        assertJsonEnvelopeProperties(notification, resultString, ID_KEY, FROM_KEY, TO_KEY, PP_KEY, METADATA_KEY);

        assertThatJson(resultString).node(EVENT_KEY).isEqualTo(notification.getEvent().toString().toLowerCase());

        assertThatJson(resultString).node(JsonConstants.Notification.REASON_KEY).isAbsent();
    }

    //endregion Notification

    //endregion serialize

    //region deserialize method

    //region Session

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

        // Act
        Envelope envelope = target.deserialize(json);

        // Assert
        assertThat(envelope).isInstanceOf(Session.class);

        Session session = (Session)envelope;
        assertThat(session.getId()).isEqualTo(id);
        assertThat(session.getFrom()).isEqualTo(from);
        assertThat(session.getTo()).isEqualTo(to);
        assertThat(session.getState()).isEqualTo(state);

        assertThat(session.getReason()).isNotNull();
        assertThat(session.getReason().getCode()).isEqualTo(reasonCode);
    }

    @Test
    public void deserialize_SessionAuthenticatingWithPlainAuthentication_ReturnsValidInstance() {
        // Arrange
        String json = "{\"state\":\"authenticating\",\"scheme\":\"plain\",\"authentication\":{\"password\":\"Zg==\"},\"id\":\"ec9c196c-da09-43b0-923b-8ec162705c32\",\"from\":\"andre@takenet.com.br/MINELLI-NOTE\"}";

        // Act
        Envelope envelope = target.deserialize(json);

        // Assert
        assertThat(envelope).isInstanceOf(Session.class);

        Session session = (Session)envelope;
        assertThat(session.getScheme()).isEqualTo(AuthenticationScheme.Plain);
        assertThat(session.getAuthentication()).isInstanceOf(PlainAuthentication.class);

        PlainAuthentication authentication = (PlainAuthentication)session.getAuthentication();
        assertThat(authentication.getPassword()).isNotNull().isNotEmpty();
    }

    @Test
    public void deserialize_SessionAuthenticatingWithGuestAuthentication_ReturnsValidInstance() {
        // Arrange
        String json = "{\"state\":\"authenticating\",\"scheme\":\"guest\",\"id\":\"feeb88e2-c209-40cd-b8ab-e14aeebe57ab\",\"from\":\"ca6829ff-1ac8-4dad-ad78-c25a3e4f8f7b@takenet.com.br/MINELLI-NOTE\"}";

        // Act
        Envelope envelope = target.deserialize(json);

        // Assert
        assertThat(envelope).isInstanceOf(Session.class);

        Session session = (Session)envelope;
        assertThat(session.getScheme()).isEqualTo(AuthenticationScheme.Guest);
        assertThat(session.getAuthentication()).isInstanceOf(GuestAuthentication.class);
    }

    //endregion Session

    //region Command

    @Test
    public void deserialize_AbsoluteUriRequestCommand_ReturnsValidInstance() {
        // Arrange
        CommandMethod method = Get;
        UUID id = UUID.randomUUID();

        Node from = createNode();
        Node pp = createNode();
        Node to = createNode();

        String randomKey1 = "randomString1";
        String randomKey2 = "randomString2";
        String randomString1 = createRandomString(50);
        String randomString2 = createRandomString(50);

        LimeUri resourceUri = createAbsoluteLimeUri();

        String json = StringUtils.format("{\"uri\":\"{0}\",\"method\":\"get\",\"id\":\"{1}\",\"from\":\"{2}\",\"pp\":\"{3}\",\"to\":\"{4}\",\"metadata\":{\"{5}\":\"{6}\",\"{7}\":\"{8}\"}}",
                resourceUri,
                id,
                from,
                pp,
                to,
                randomKey1,
                randomString1,
                randomKey2,
                randomString2
        );

        // Act
        Envelope envelope = target.deserialize(json);

        // Assert
        assertThat(envelope).isInstanceOf(Command.class);

        Command command = (Command)envelope;

        assertThat(command.getId()).isEqualTo(id);
        assertThat(command.getFrom()).isEqualTo(from);
        assertThat(command.getTo()).isEqualTo(to);
        assertThat(command.getPp()).isEqualTo(pp);
        assertThat(command.getMetadata()).isNotNull();
        assertThat(command.getMetadata()).containsKey(randomKey1);
        assertThat(command.getMetadata().get(randomKey1)).isEqualTo(randomString1);
        assertThat(command.getMetadata()).containsKey(randomKey2);
        assertThat(command.getMetadata().get(randomKey2)).isEqualTo(randomString2);

        assertThat(command.getMethod()).isEqualTo(method);
        assertThat(command.getUri()).isNotNull();
        assertThat(command.getUri()).isEqualTo(resourceUri);

        assertThat(command.getType()).isNull();
        assertThat(command.getResource()).isNull();
    }

    //endregion Command

    //region Message

    @Test
    public void deserialize_UnknownPlainContentMessage_ReturnsValidInstance()
    {
        UUID id = UUID.randomUUID();
        Node from = createNode();
        Node pp = createNode();
        Node to = createNode();

        String randomKey1 = "randomString1";
        String randomKey2 = "randomString2";
        String randomString1 = createRandomString(50);
        String randomString2 = createRandomString(50);

        MediaType type = createPlainMediaType();
        String text = createRandomString(50);

        String json = StringUtils.format(
                "{\"type\":\"{0}\",\"content\":\"{1}\",\"id\":\"{2}\",\"from\":\"{3}\",\"pp\":\"{4}\",\"to\":\"{5}\",\"metadata\":{\"{6}\":\"{7}\",\"{8}\":\"{9}\"}}",
                type,
                text,
                id,
                from,
                pp,
                to,
                randomKey1,
                randomString1,
                randomKey2,
                randomString2
        );

        Envelope envelope = target.deserialize(json);

        assertThat(envelope instanceof Message);

        Message message = (Message)envelope;
        assertEquals(id, message.getId());
        assertEquals(from, message.getFrom());
        assertEquals(pp, message.getPp());
        assertEquals(to, message.getTo());
        assertNotNull(message.getMetadata());
        assertTrue(message.getMetadata().containsKey(randomKey1));
        assertEquals(message.getMetadata().get(randomKey1), randomString1);
        assertTrue(message.getMetadata().containsKey(randomKey2));
        assertEquals(message.getMetadata().get(randomKey2), randomString2);

        assertNotNull(message.getType());
        assertEquals(message.getType(), type);

        assertTrue(message.getContent() instanceof PlainDocument);

        PlainDocument content = (PlainDocument)message.getContent();
        assertEquals(text, content.getValue());
    }

    @Test
    public void deserialize_UnknownJsonContentMessage_ReturnsValidInstance()
    {
        UUID id = UUID.randomUUID();
        Node from = createNode();
        Node pp = createNode();
        Node to = createNode();

        String randomKey1 = "randomString1";
        String randomKey2 = "randomString2";
        String randomString1 = createRandomString(50);
        String randomString2 = createRandomString(50);


        MediaType type = createJsonMediaType();

        String propertyName1 = createRandomString(10);
        String propertyName2 = createRandomString(10);
        String propertyValue1 = createRandomString(10);
        long propertyValue2 = createRandomLong();

        String json = StringUtils.format(
                "{\"type\":\"{0}\",\"content\":{\"{1}\":\"{2}\",\"{3}\":{4}},\"id\":\"{5}\",\"from\":\"{6}\",\"pp\":\"{7}\",\"to\":\"{8}\",\"metadata\":{\"{9}\":\"{10}\",\"{11}\":\"{12}\"}}",
                type,
                propertyName1,
                propertyValue1,
                propertyName2,
                propertyValue2,
                id,
                from,
                pp,
                to,
                randomKey1,
                randomString1,
                randomKey2,
                randomString2
        );

        Envelope envelope = target.deserialize(json);

        assertThat(envelope instanceof Message);

        Message message = (Message)envelope;

        assertEquals(id, message.getId());
        assertEquals(from, message.getFrom());
        assertEquals(pp, message.getPp());
        assertEquals(to, message.getTo());

        assertNotNull(message.getMetadata());

        assertTrue(message.getMetadata().containsKey(randomKey1));
        assertEquals(message.getMetadata().get(randomKey1), randomString1);

        assertTrue(message.getMetadata().containsKey(randomKey2));
        assertEquals(message.getMetadata().get(randomKey2), randomString2);

        assertNotNull(message.getType());
        assertEquals(message.getType(), type);

        assertTrue(message.getContent() instanceof JsonDocument);

        JsonDocument content = (JsonDocument)message.getContent();

        assertTrue(content.containsKey(propertyName1));
        assertEquals(content.get(propertyName1), propertyValue1);

        assertTrue(content.containsKey(propertyName2));
        assertEquals(content.get(propertyName2), propertyValue2);
    }

    @Test
    public void deserialize_GenericJsonContentMessage_ReturnsValidInstance()
    {
        UUID id = UUID.randomUUID();
        Node from = createNode();
        Node pp = createNode();
        Node to = createNode();

        String randomKey1 = "randomString1";
        String randomKey2 = "randomString2";
        String randomString1 = createRandomString(50);
        String randomString2 = createRandomString(50);

        MediaType type = new MediaType(MediaType.DiscreteTypes.Application, MediaType.SubTypes.JSON, null);

        String propertyName1 = createRandomString(10);
        String propertyName2 = createRandomString(10);
        String propertyValue1 = createRandomString(10);
        long propertyValue2 = (long) createRandomLong();


        String json = StringUtils.format(
                "{\"type\":\"{0}\",\"content\":{\"{1}\":\"{2}\",\"{3}\":{4}},\"id\":\"{5}\",\"from\":\"{6}\",\"pp\":\"{7}\",\"to\":\"{8}\",\"metadata\":{\"{9}\":\"{10}\",\"{11}\":\"{12}\"}}",
                type,
                propertyName1,
                propertyValue1,
                propertyName2,
                propertyValue2,
                id,
                from,
                pp,
                to,
                randomKey1,
                randomString1,
                randomKey2,
                randomString2
        );

        Envelope envelope = target.deserialize(json);

        assertTrue(envelope instanceof Message);

        Message message = (Message)envelope;

        assertEquals(id, message.getId());
        assertEquals(from, message.getFrom());
        assertEquals(pp, message.getPp());
        assertEquals(to, message.getTo());

        assertNotNull(message.getMetadata());

        assertTrue(message.getMetadata().containsKey(randomKey1));
        assertEquals(message.getMetadata().get(randomKey1), randomString1);

        assertTrue(message.getMetadata().containsKey(randomKey2));
        assertEquals(message.getMetadata().get(randomKey2), randomString2);

        assertNotNull(message.getType());
        assertEquals(message.getType(), type);

        assertTrue(message.getContent() instanceof JsonDocument);

        JsonDocument content = (JsonDocument)message.getContent();

        assertTrue(content.containsKey(propertyName1));
        assertEquals(content.get(propertyName1), propertyValue1);

        assertTrue(content.containsKey(propertyName2));
        assertEquals(content.get(propertyName2), propertyValue2);

    }

    //endregion Message

    //region Notification

    @Test
    public void deserialize_ReceivedNotification_ReturnsValidInstance()
    {
        UUID id = UUID.randomUUID();
        Node from = createNode();
        Node  pp = createNode();
        Node to = createNode();

        String randomKey1 = "randomString1";
        String randomKey2 = "randomString2";
        String randomString1 = createRandomString(50);
        String randomString2 = createRandomString(50);

        Notification.Event event = Notification.Event.Received;

        String json = StringUtils.format(
                "{\"event\":\"{0}\",\"id\":\"{1}\",\"from\":\"{2}\",\"pp\":\"{3}\",\"to\":\"{4}\",\"metadata\":{\"{5}\":\"{6}\",\"{7}\":\"{8}\"}}",
        event.toString().toLowerCase(),
            id,
            from,
            pp,
            to,
            randomKey1,
            randomString1,
            randomKey2,
            randomString2
        );

        Envelope envelope = target.deserialize(json);

        assertTrue(envelope instanceof Notification);

        Notification notification = (Notification) envelope;
        assertEquals(id, notification.getId());
        assertEquals(from, notification.getFrom());
        assertEquals(pp, notification.getPp());
        assertEquals(to, notification.getTo());
        assertNotNull(notification.getMetadata());
        assertTrue(notification.getMetadata().containsKey(randomKey1));
        assertTrue(notification.getMetadata().containsKey(randomKey2));
        assertEquals(notification.getMetadata().get(randomKey1), randomString1);
        assertEquals(notification.getMetadata().get(randomKey2), randomString2);

        assertEquals(event, notification.getEvent());

        assertNull(notification.getReason());
    }

    @Test
    public void deserialize_FailedNotification_ReturnsValidInstance()
    {
        Notification.Event event = Notification.Event.Received;
        int reasonCode = createRandomInt(100);
        String reasonDescription = createRandomString(100);

        UUID id = UUID.randomUUID();
        Node from = createNode();
        Node to = createNode();

        String json = StringUtils.format(
                "{\"event\":\"{0}\",\"id\":\"{1}\",\"from\":\"{2}\",\"to\":\"{3}\",\"reason\":{\"code\":{4},\"description\":\"{5}\"}}",
                event.toString().toLowerCase(),
                id,
                from,
                to,
                reasonCode,
                reasonDescription);

        Envelope envelope = target.deserialize(json);

        assertTrue(envelope instanceof Notification);
        Notification notification = (Notification) envelope;
        assertEquals(id, notification.getId());
        assertEquals(from, notification.getFrom());
        assertEquals(to, notification.getTo());
        assertEquals(event, notification.getEvent());

        assertNull(notification.getPp());
        assertNull(notification.getMetadata());

        assertNotNull(notification.getReason());

        assertEquals(reasonCode, notification.getReason().getCode());
        assertEquals(reasonDescription, notification.getReason().getDescription());
    }

    //endregion Notification

    //endregion deserialize

    public static void assertJsonEnvelopeProperties(Envelope expected, String jsonString, String... properties) {
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