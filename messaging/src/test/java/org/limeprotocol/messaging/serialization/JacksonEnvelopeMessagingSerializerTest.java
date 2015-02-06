package org.limeprotocol.messaging.serialization;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.limeprotocol.Command;
import org.limeprotocol.Envelope;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import org.limeprotocol.messaging.contents.PlainText;
import org.limeprotocol.messaging.resource.Capability;
import org.limeprotocol.messaging.resource.Receipt;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;
import org.limeprotocol.testHelpers.JsonConstants;
import org.limeprotocol.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.limeprotocol.Command.CommandMethod.*;
import static org.limeprotocol.Notification.*;
import static org.limeprotocol.messaging.testHelpers.MessagingJsonConstants.Capability.*;
import static org.limeprotocol.messaging.testHelpers.MessagingTestDummy.*;
import static org.limeprotocol.serialization.JacksonEnvelopeSerializerTest.assertJsonEnvelopeProperties;
import static org.limeprotocol.testHelpers.JsonConstants.Command.*;
import static org.limeprotocol.testHelpers.JsonConstants.Envelope.*;
import static org.limeprotocol.testHelpers.TestDummy.*;

public class JacksonEnvelopeMessagingSerializerTest {

    private JacksonEnvelopeSerializer target;

    @Before
    public void setUp() throws Exception {
        target = new JacksonEnvelopeSerializer();
    }

    //region serialize method

    //region Message

    @Test
    public void serialize_TextMessage_ReturnsValidJsonString()
    {
        PlainText content = createTextContent();
        Message message = createMessage(content);
        message.setPp(createNode());

        message.setMetadata(createRandomMetadata());

        String resultString = target.serialize(message);

        assertJsonEnvelopeProperties(message, resultString, ID_KEY, FROM_KEY, TO_KEY, PP_KEY, METADATA_KEY);

        assertThatJson(resultString).node(JsonConstants.Message.TYPE_KEY).isEqualTo(message.getType().toString());

        assertThatJson(resultString).node(JsonConstants.Message.CONTENT_KEY).isPresent();
        assertThatJson(resultString).node(JsonConstants.PlainText.CONTENT_TEXT_KEY).isEqualTo(content.getText());
    }

    @Test
    public void serialize_FireAndForgetTextMessage_ReturnsValidJsonString()
    {
        PlainText content = createTextContent();
        Message message = createMessage(content);
        message.setId(null);

        String resultString = target.serialize(message);

        assertJsonEnvelopeProperties(message, resultString, FROM_KEY, TO_KEY);

        assertThatJson(resultString).node(JsonConstants.Message.TYPE_KEY).isEqualTo(message.getType().toString());
        assertThatJson(resultString).node(JsonConstants.Message.CONTENT_KEY).isPresent();

        assertThatJson(resultString).node(JsonConstants.PlainText.CONTENT_TEXT_KEY).isEqualTo(content.getText());
    }

    //endregion Message

    //region Command

    @Test
    public void serialize_CapabilityRequestCommand_ReturnsValidJsonString()
    {
        Capability resource = createCapability();
        Command command = createCommand(resource);
        command.setPp(createNode());
        command.setMethod(Command.CommandMethod.Get);

        String metadataKey1 = "randomString1";
        String metadataValue1 = createRandomString(50);
        String metadataKey2 = "randomString2";
        String metadataValue2 = createRandomString(50);
        Map<String, String> metadata = new HashMap<>();
        metadata.put(metadataKey1, metadataValue1);
        metadata.put(metadataKey2, metadataValue2);
        command.setMetadata(metadata);

        String resultString = target.serialize(command);

        assertJsonEnvelopeProperties(command, resultString, ID_KEY, FROM_KEY, PP_KEY, TO_KEY, METADATA_KEY );

        assertThatJson(resultString).node(METHOD_KEY).isEqualTo(command.getMethod().toString().toLowerCase());

        assertThatJson(resultString).node(RESOURCE_KEY).isPresent();
        assertThatJson(resultString).node(TYPE_KEY).isEqualTo(command.getResource().getMediaType().toString());

        assertThatJson(resultString).node(RESOURCE_CONTENT_TYPES_KEY).isArray().ofLength(3);
        assertThatJson(resultString).node(RESOURCE_CONTENT_TYPES_KEY + "[0]").isEqualTo(resource.getContentTypes()[0].toString());
        assertThatJson(resultString).node(RESOURCE_CONTENT_TYPES_KEY + "[1]").isEqualTo(resource.getContentTypes()[1].toString());
        assertThatJson(resultString).node(RESOURCE_CONTENT_TYPES_KEY + "[2]").isEqualTo(resource.getContentTypes()[2].toString());

        assertThatJson(resultString).node(RESOURCE_TYPES_KEY).isArray().ofLength(3);
        assertThatJson(resultString).node(RESOURCE_TYPES_KEY + "[0]").isEqualTo(resource.getResourceTypes()[0].toString());
        assertThatJson(resultString).node(RESOURCE_TYPES_KEY + "[1]").isEqualTo(resource.getResourceTypes()[1].toString());
        assertThatJson(resultString).node(RESOURCE_TYPES_KEY + "[2]").isEqualTo(resource.getResourceTypes()[2].toString());

        assertThatJson(resultString).node(STATUS_KEY).isAbsent();
        assertThatJson(resultString).node(REASON_KEY).isAbsent();
    }

    //endregion Command

    //endregion Message

    //endregion serialize method

    //region deserialize method

    //region Message

//    public void Deserialize_TextMessage_ReturnsValidInstance()
//    {
//        var target = GetTarget();
//
//        var id = Guid.NewGuid();
//        var from = DataUtil.CreateNode();
//        var pp = DataUtil.CreateNode();
//        var to = DataUtil.CreateNode();
//
//        string randomKey1 = "randomString1";
//        string randomKey2 = "randomString2";
//        string randomString1 = DataUtil.CreateRandomString(50);
//        string randomString2 = DataUtil.CreateRandomString(50);
//
//        var text = DataUtil.CreateRandomString(50);
//
//        string json = string.Format(
//                "{{\"type\":\"text/plain\",\"content\":\"{0}\",\"id\":\"{1}\",\"from\":\"{2}\",\"pp\":\"{3}\",\"to\":\"{4}\",\"metadata\":{{\"{5}\":\"{6}\",\"{7}\":\"{8}\"}}}}",
//                text,
//                id,
//                from,
//                pp,
//                to,
//                randomKey1,
//                randomString1,
//                randomKey2,
//                randomString2
//        );
//
//        var envelope = target.Deserialize(json);
//
//        Assert.IsTrue(envelope is Message);
//
//        var message = (Message)envelope;
//        Assert.AreEqual(id, message.Id);
//        Assert.AreEqual(from, message.From);
//        Assert.AreEqual(pp, message.Pp);
//        Assert.AreEqual(to, message.To);
//        Assert.IsNotNull(message.Metadata);
//        Assert.IsTrue(message.Metadata.ContainsKey(randomKey1));
//        Assert.AreEqual(message.Metadata[randomKey1], randomString1);
//        Assert.IsTrue(message.Metadata.ContainsKey(randomKey2));
//        Assert.AreEqual(message.Metadata[randomKey2], randomString2);
//
//        Assert.IsTrue(message.Content is PlainText);
//
//        var textContent = (PlainText)message.Content;
//        Assert.AreEqual(text, textContent.Text);
//    }
//
//    [Test]
//            [Category("Deserialize")]
//    public void Deserialize_ChatStateMessage_ReturnsValidInstance()
//    {
//        var target = GetTarget();
//
//        var id = Guid.NewGuid();
//        var from = DataUtil.CreateNode();
//        var pp = DataUtil.CreateNode();
//        var to = DataUtil.CreateNode();
//
//        string randomKey1 = "randomString1";
//        string randomKey2 = "randomString2";
//        string randomString1 = DataUtil.CreateRandomString(50);
//        string randomString2 = DataUtil.CreateRandomString(50);
//
//        var state = ChatStateEvent.Deleting;
//
//        string json = string.Format(
//                "{{\"type\":\"application/vnd.lime.chatstate+json\",\"content\":{{\"state\":\"{0}\"}},\"id\":\"{1}\",\"from\":\"{2}\",\"pp\":\"{3}\",\"to\":\"{4}\",\"metadata\":{{\"{5}\":\"{6}\",\"{7}\":\"{8}\"}}}}",
//                state.ToString().ToLowerInvariant(),
//                id,
//                from,
//                pp,
//                to,
//                randomKey1,
//                randomString1,
//                randomKey2,
//                randomString2
//        );
//
//        var envelope = target.Deserialize(json);
//
//        Assert.IsTrue(envelope is Message);
//
//        var message = (Message)envelope;
//        Assert.AreEqual(id, message.Id);
//        Assert.AreEqual(from, message.From);
//        Assert.AreEqual(pp, message.Pp);
//        Assert.AreEqual(to, message.To);
//        Assert.IsNotNull(message.Metadata);
//        Assert.IsTrue(message.Metadata.ContainsKey(randomKey1));
//        Assert.AreEqual(message.Metadata[randomKey1], randomString1);
//        Assert.IsTrue(message.Metadata.ContainsKey(randomKey2));
//        Assert.AreEqual(message.Metadata[randomKey2], randomString2);
//
//        Assert.IsTrue(message.Content is ChatState);
//
//        var textContent = (ChatState)message.Content;
//        Assert.AreEqual(state, textContent.State);
//    }
//
//    public void Deserialize_FireAndForgetTextMessage_ReturnsValidInstance()
//    {
//        var target = GetTarget();
//
//        var from = DataUtil.CreateNode();
//        var to = DataUtil.CreateNode();
//
//        var text = DataUtil.CreateRandomString(50);
//
//        string json = string.Format(
//                "{{\"type\":\"text/plain\",\"content\":\"{0}\",\"from\":\"{1}\",\"to\":\"{2}\"}}",
//                text,
//                from,
//                to
//        );
//
//        var envelope = target.Deserialize(json);
//
//        Assert.IsTrue(envelope is Message);
//
//        var message = (Message)envelope;
//        Assert.AreEqual(from, message.From);
//        Assert.AreEqual(to, message.To);
//
//        Assert.AreEqual(message.Id, Guid.Empty);
//        Assert.IsNull(message.Pp);
//        Assert.IsNull(message.Metadata);
//
//        Assert.IsTrue(message.Content is PlainText);
//        var textContent = (PlainText)message.Content;
//        Assert.AreEqual(text, textContent.Text);
//    }
//
//    [Test]
//            [Category("Deserialize")]
//    public void Deserialize_FireAndForgetChatStateMessage_ReturnsValidInstance()
//    {
//        var target = GetTarget();
//
//        var from = DataUtil.CreateNode();
//        var to = DataUtil.CreateNode();
//
//        var state = ChatStateEvent.Composing;
//
//        string json = string.Format(
//                "{{\"type\":\"application/vnd.lime.chatstate+json\",\"content\":{{\"state\":\"{0}\"}},\"from\":\"{1}\",\"to\":\"{2}\"}}",
//                state.ToString().ToCamelCase(),
//                from,
//                to
//        );
//
//        var envelope = target.Deserialize(json);
//
//        Assert.IsTrue(envelope is Message);
//
//        var message = (Message)envelope;
//        Assert.AreEqual(from, message.From);
//        Assert.AreEqual(to, message.To);
//
//        Assert.AreEqual(message.Id, Guid.Empty);
//        Assert.IsNull(message.Pp);
//        Assert.IsNull(message.Metadata);
//
//        Assert.IsTrue(message.Content is ChatState);
//        var textContent = (ChatState)message.Content;
//        Assert.AreEqual(state, textContent.State);
//    }

    //endregion Message

    //region Command

    @Test
    public void deserialize_ReceiptRequestCommand_ReturnsValidInstance() {
        // Arrange
        Command.CommandMethod method = Set;
        UUID id = UUID.randomUUID();

        String json = StringUtils.format(
                "{\"type\":\"application/vnd.lime.receipt+json\",\"resource\":{\"events\":[\"dispatched\",\"received\"]},\"method\":\"{0}\",\"id\":\"{1}\"}",
                method.toString(),
                id);

        // Act
        Envelope envelope = target.deserialize(json);

        // Assert
        assertThat(envelope).isInstanceOf(Command.class);

        Command command = (Command)envelope;

        assertThat(command.getId()).isEqualTo(id);
        assertThat(command.getFrom()).isNull();
        assertThat(command.getTo()).isNull();
        assertThat(command.getPp()).isNull();
        assertThat(command.getMetadata()).isNull();

        assertThat(command.getMethod()).isEqualTo(method);

        assertThat(command.getType().toString()).isEqualTo(Receipt.MIME_TYPE);
        assertThat(command.getResource()).isNotNull().isInstanceOf(Receipt.class);
        Receipt receipt = (Receipt) command.getResource();
        assertThat(receipt.getEvents()).containsOnly(new Event[] {Event.Dispatched, Event.Received });

        assertThat(command.getUri()).isNull();
    }

    //endregion Command

    //endregion deserialize method
}