package org.limeprotocol.messaging.serialization;

import org.fest.assertions.core.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.limeprotocol.*;
import org.limeprotocol.messaging.contents.PlainText;
import org.limeprotocol.messaging.resource.Capability;
import org.limeprotocol.messaging.resource.Contact;
import org.limeprotocol.messaging.resource.Receipt;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;
import org.limeprotocol.testHelpers.JsonConstants;
import org.limeprotocol.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.limeprotocol.Command.*;
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
        command.setMethod(CommandMethod.Get);

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

        assertThatJson(resultString).node(CONTENT_TYPES_KEY).isEqualTo(resource.getContentTypes());
        assertThatJson(resultString).node(RESOURCE_TYPES_KEY).isEqualTo(resource.getResourceTypes());

        assertThatJson(resultString).node(STATUS_KEY).isPresent();
        assertThatJson(resultString).node(REASON_KEY).isPresent();
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
        CommandMethod method = Set;
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
        assertThat(receipt.getEvents()).containsOnly(new Event[]{Event.Dispatched, Event.Received});

        assertThat(command.getUri()).isNull();
    }

    @Test
    public void deserialize_ContactCollectionResponseCommand_ReturnsValidInstance() {
        // Arrange
        Identity identity1 = createIdentity();
        String name1 = createRandomString(50);
        Identity identity2 = createIdentity();
        String name2 = createRandomString(50);
        Identity identity3 = createIdentity();
        String name3 = createRandomString(50);

        CommandMethod method = CommandMethod.Get;

        UUID id = UUID.randomUUID();
        Node from = createNode();
        Node  pp = createNode();
        Node to = createNode();

        String randomKey1 = "randomString1";
        String randomKey2 = "randomString2";
        String randomString1 = createRandomString(50);
        String randomString2 = createRandomString(50);

        String json = StringUtils.format(
                "{\"type\":\"application/vnd.lime.collection+json\",\"resource\":{\"itemType\":\"application/vnd.lime.contact+json\",\"total\":3,\"items\":[{\"identity\":\"{0}\",\"name\":\"{1}\",\"isPending\":true,\"shareAccountInfo\":false},{\"identity\":\"{2}\",\"name\":\"{3}\",\"sharePresence\":false},{\"identity\":\"{4}\",\"name\":\"{5}\",\"isPending\":true,\"sharePresence\":false}]},\"method\":\"get\",\"status\":\"success\",\"id\":\"{6}\",\"from\":\"{7}\",\"pp\":\"{8}\",\"to\":\"{9}\",\"metadata\":{\"{10}\":\"{11}\",\"{12}\":\"{13}\"}}",
                identity1,
                name1,
                identity2,
                name2,
                identity3,
                name3,
                id,
                from,
                pp,
                to,
                randomKey1,
                randomString1,
                randomKey2,
                randomString2);

        // Act
        Envelope envelope = target.deserialize(json);

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

        assertThat(command.getType().toString()).isEqualTo(DocumentCollection.MIME_TYPE);
        assertThat(command.getResource()).isNotNull().isInstanceOf(DocumentCollection.class);

        DocumentCollection documents = (DocumentCollection)command.getResource();

        Document[] items = documents.getItems();
        assertThat(items).isNotNull().hasSize(3);

        Contact[] contacts = Arrays.copyOf(items, items.length, Contact[].class);

        assertThat(contacts[0].getIdentity()).isEqualTo(identity1);
        assertThat(contacts[0].getName()).isEqualTo(name1);
        assertThat(contacts[0].isPending()).isNotNull().isTrue();
        assertThat(contacts[0].getShareAccountInfo()).isNotNull().isFalse();
        assertThat(contacts[0].getSharePresence()).isNull();

        assertThat(contacts[1].getIdentity()).isEqualTo(identity2);
        assertThat(contacts[1].getName()).isEqualTo(name2);
        assertThat(contacts[1].isPending()).isNull();
        assertThat(contacts[1].getShareAccountInfo()).isNull();
        assertThat(contacts[1].getSharePresence()).isNotNull().isFalse();

        assertThat(contacts[2].getIdentity()).isEqualTo(identity3);
        assertThat(contacts[2].getName()).isEqualTo(name3);
        assertThat(contacts[2].isPending()).isNotNull().isTrue();
        assertThat(contacts[2].getShareAccountInfo()).isNull();
        assertThat(contacts[2].getSharePresence()).isNotNull().isFalse();
    }


    //endregion Command

    //endregion deserialize method
}