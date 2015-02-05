package org.limeprotocol.serialization;

import org.junit.Before;
import org.junit.Test;
import org.limeprotocol.*;
import org.limeprotocol.Session.SessionState;
import org.limeprotocol.messaging.contents.PlainText;
import org.limeprotocol.security.*;
import org.limeprotocol.testHelpers.JsonConstants;
import org.limeprotocol.util.StringUtils;

import java.util.HashMap;
import java.util.UUID;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.limeprotocol.security.Authentication.AuthenticationScheme;
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
    public void serialize_AuthenticatingSession_ReturnsValidJsonString() {
        // Arrange
        Session session = createSession(SessionState.Authenticating);
        PlainAuthentication plainAuthentication = createPlainAuthentication();
        session.setAuthentication(plainAuthentication);

        String metadataKey1 = "randomString1";
        String metadataValue1 = createRandomString(50);
        String metadataKey2 = "randomString2";
        String metadataValue2 = createRandomString(50);
        session.setMetadata(new HashMap<String, String>());
        session.getMetadata().put(metadataKey1, metadataValue1);
        session.getMetadata().put(metadataKey2, metadataValue2);

        // Act
        String resultString = target.serialize(session);

        // Assert
        assertThatJson(resultString).node(JsonConstants.Envelope.ID_KEY).isEqualTo(session.getId());
        assertThatJson(resultString).node(JsonConstants.Envelope.FROM_KEY).isEqualTo(session.getFrom().toString());
        assertThatJson(resultString).node(JsonConstants.Envelope.TO_KEY).isEqualTo(session.getTo().toString());
        assertThatJson(resultString).node(JsonConstants.Session.STATE_KEY).isEqualTo(session.getState().toString().toLowerCase());
        assertThatJson(resultString).node(JsonConstants.Envelope.getMetadataKeyFromRoot(metadataKey1)).isEqualTo(metadataValue1);
        assertThatJson(resultString).node(JsonConstants.Envelope.getMetadataKeyFromRoot(metadataKey2)).isEqualTo(metadataValue2);
        assertThatJson(resultString).node(JsonConstants.Session.AUTHENTICATION_KEY).isPresent();
        assertThatJson(resultString).node(JsonConstants.PlainAuthentication.PASSWORK_KEY_FROM_ROOT).isEqualTo(plainAuthentication.getPassword());

        assertThatJson(resultString).node(JsonConstants.Envelope.PP_KEY).isAbsent();
        assertThatJson(resultString).node(JsonConstants.Session.REASON_KEY).isAbsent();
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
        assertThatJson(resultString).node(JsonConstants.Envelope.ID_KEY).isEqualTo(session.getId());
        assertThatJson(resultString).node(JsonConstants.Envelope.FROM_KEY).isEqualTo(session.getFrom().toString());
        assertThatJson(resultString).node(JsonConstants.Envelope.TO_KEY).isEqualTo(session.getTo().toString());
        assertThatJson(resultString).node(JsonConstants.Session.STATE_KEY).isEqualTo(session.getState().toString().toLowerCase());
        assertThatJson(resultString).node(JsonConstants.Session.REASON_KEY).isPresent();
        assertThatJson(resultString).node(JsonConstants.Reason.CODE_KEY_FROM_ROOT).isEqualTo(session.getReason().getCode());
        assertThatJson(resultString).node(JsonConstants.Reason.DESCRIPTION_KEY_FROM_ROOT).isEqualTo(session.getReason().getDescription());

        assertThatJson(resultString).node(JsonConstants.Envelope.PP_KEY).isAbsent();
        assertThatJson(resultString).node(JsonConstants.Envelope.METADATA_KEY).isAbsent();
        assertThatJson(resultString).node(JsonConstants.Session.AUTHENTICATION_KEY).isAbsent();
    }

    //endregion Session

    //region Command

//    @Test
//    public void serialize_AbsoluteUriRequestCommand_ReturnsValidJsonString()
//    {
//
//        Command command = TestDummy.createCommand();
//        command.setPp(TestDummy.createNode());
//        command.setUri(TestDummy.createAbsoluteLimeUri());
//
//
//        String metadataKey1 = "randomString1";
//        String metadataValue1 = TestDummy.createRandomString(50);
//        String metadataKey2 = "randomString2";
//        String metadataValue2 = TestDummy.createRandomString(50);
//        command.Metadata = new Dictionary<string, string>();
//        command.Metadata.Add(metadataKey1, metadataValue1);
//        command.Metadata.Add(metadataKey2, metadataValue2);
//
//        var resultString = target.Serialize(command);
//
//        Assert.IsTrue(resultString.HasValidJsonStackedBrackets());
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.ID_KEY, command.Id));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.FROM_KEY, command.From));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.PP_KEY, command.Pp));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.TO_KEY, command.To));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Command.METHOD_KEY, command.Method));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Command.URI_KEY, command.Uri));
//
//
//        Assert.IsTrue(resultString.ContainsJsonProperty(Command.METHOD_KEY, command.Method));
//        Assert.IsTrue(resultString.ContainsJsonProperty(metadataKey1, metadataValue1));
//        Assert.IsTrue(resultString.ContainsJsonProperty(metadataKey2, metadataValue2));
//
//        Assert.IsFalse(resultString.ContainsJsonKey(Command.STATUS_KEY));
//        Assert.IsFalse(resultString.ContainsJsonKey(Command.REASON_KEY));
//        Assert.IsFalse(resultString.ContainsJsonKey(Command.TYPE_KEY));
//        Assert.IsFalse(resultString.ContainsJsonKey(Command.RESOURCE_KEY));
//    }
    //endregion Command

    //region Message

    public void serialize_TextMessage_ReturnsValidJsonString()
    {
        PlainText content = createTextContent();
        Message message = createMessage(content);
        message.setPp(createNode());

        String metadataKey1 = "randomString1";
        String metadataValue1 = createRandomString(50);
        String metadataKey2 = "randomString2";
        String metadataValue2 = createRandomString(50);
        message.setMetadata(new HashMap<String, String>());
        message.getMetadata().put(metadataKey1, metadataValue1);
        message.getMetadata().put(metadataKey2, metadataValue2);


        String resultString = target.serialize(message);

        assertThatJson(resultString).node(JsonConstants.Envelope.ID_KEY).isEqualTo(message.getId());
        assertThatJson(resultString).node(JsonConstants.Envelope.FROM_KEY).isEqualTo(message.getFrom().toString());
        assertThatJson(resultString).node(JsonConstants.Envelope.TO_KEY).isEqualTo(message.getTo().toString());
        assertThatJson(resultString).node(JsonConstants.Envelope.PP_KEY).isEqualTo(message.getPp().toString());
        assertThatJson(resultString).node(JsonConstants.Message.TYPE_KEY).isEqualTo(message.getType().toString());

        assertThatJson(resultString).node(JsonConstants.Message.CONTENT_KEY).isPresent();
        assertThatJson(resultString).node(JsonConstants.Message.CONTENT_KEY).isEqualTo(content.getText());

        assertThatJson(resultString).node(JsonConstants.Envelope.getMetadataKeyFromRoot(metadataKey1)).isEqualTo(metadataValue1);
        assertThatJson(resultString).node(JsonConstants.Envelope.getMetadataKeyFromRoot(metadataKey2)).isEqualTo(metadataValue2);

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

    //endregion serialize

    //region deserialize method

    @Test
    public void deserialize_AuthenticatingSession_ReturnsValidInstance() {
        // Arrange
        UUID id = UUID.randomUUID();
        Node from = createNode();
        Node pp = createNode();
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
        Node pp = createNode();
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

}