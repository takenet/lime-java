package org.limeprotocol.messaging.serialization;

import org.junit.Before;
import org.junit.Test;
import org.limeprotocol.*;
import org.limeprotocol.messaging.Registrator;
import org.limeprotocol.messaging.contents.ChatState;
import org.limeprotocol.messaging.contents.PlainText;
import org.limeprotocol.messaging.resources.Account;
import org.limeprotocol.messaging.resources.Capability;
import org.limeprotocol.messaging.resources.Contact;
import org.limeprotocol.messaging.resources.Receipt;
import org.limeprotocol.messaging.testHelpers.MessagingJsonConstants;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;
import org.limeprotocol.testHelpers.JsonConstants;
import org.limeprotocol.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.limeprotocol.Command.CommandMethod.SET;
import static org.limeprotocol.Command.CommandMethod.GET;
import static org.limeprotocol.Notification.Event;
import static org.limeprotocol.messaging.testHelpers.MessagingJsonConstants.Account.*;
import static org.limeprotocol.messaging.testHelpers.MessagingJsonConstants.Capability.RESOURCE_CONTENT_TYPES_KEY;
import static org.limeprotocol.messaging.testHelpers.MessagingJsonConstants.Capability.RESOURCE_TYPES_KEY;
import static org.limeprotocol.messaging.testHelpers.MessagingJsonConstants.Contact.*;
import static org.limeprotocol.messaging.testHelpers.MessagingTestDummy.*;
import static org.limeprotocol.serialization.JacksonEnvelopeSerializerTest.assertJsonEnvelopeProperties;
import static org.limeprotocol.testHelpers.JsonConstants.Command.*;
import static org.limeprotocol.testHelpers.JsonConstants.DocumentCollection.*;
import static org.limeprotocol.testHelpers.JsonConstants.Envelope.*;
import static org.limeprotocol.testHelpers.Dummy.*;

public class JacksonEnvelopeMessagingSerializerTest {

    private JacksonEnvelopeSerializer target;

    @Before
    public void setUp() throws Exception {
        Registrator.registerDocuments();
        target = new JacksonEnvelopeSerializer();
    }

    //region serialize method

    //region Message

    @Test
    public void serialize_TextMessage_ReturnsValidJsonString()
    {
        PlainText content = createPlainText();
        Message message = createMessage(content);
        message.setPp(createNode());

        message.setMetadata(createRandomMetadata());

        String resultString = target.serialize(message);

        assertJsonEnvelopeProperties(message, resultString, ID_KEY, FROM_KEY, TO_KEY, PP_KEY, METADATA_KEY);

        assertThatJson(resultString).node(JsonConstants.Message.TYPE_KEY).isEqualTo(message.getType().toString());

        assertThatJson(resultString).node(JsonConstants.Message.CONTENT_KEY).isEqualTo(content.getText());
    }

    @Test
    public void serialize_FireAndForgetTextMessage_ReturnsValidJsonString()
    {
        PlainText content = createPlainText();
        Message message = createMessage(content);
        message.setId(null);

        String resultString = target.serialize(message);

        assertJsonEnvelopeProperties(message, resultString, FROM_KEY, TO_KEY);

        assertThatJson(resultString).node(JsonConstants.Message.TYPE_KEY).isEqualTo(message.getType().toString());
        assertThatJson(resultString).node(JsonConstants.Message.CONTENT_KEY).isEqualTo(content.getText());
    }

    //endregion Message

    //region Command

    @Test
    public void serialize_CapabilityRequestCommand_ReturnsValidJsonString()
    {
        Capability resource = createCapability();
        Command command = createCommand(resource);
        command.setPp(createNode());
        command.setMethod(Command.CommandMethod.GET);

        String metadataKey1 = "randomString1";
        String metadataValue1 = createRandomString(50);
        String metadataKey2 = "randomString2";
        String metadataValue2 = createRandomString(50);
        Map<String, String> metadata = new HashMap<>();
        metadata.put(metadataKey1, metadataValue1);
        metadata.put(metadataKey2, metadataValue2);
        command.setMetadata(metadata);

        String resultString = target.serialize(command);

        assertJsonEnvelopeProperties(command, resultString, ID_KEY, FROM_KEY, PP_KEY, TO_KEY, METADATA_KEY);

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

    @Test
    public void serialize_AccountRequestCommand_ReturnsValidJsonString()
    {
        Account resource = createAccount();
        Command command = createCommand(resource);
        command.setMethod(Command.CommandMethod.GET);

        String resultString = target.serialize(command);

        assertJsonEnvelopeProperties(command, resultString, ID_KEY, FROM_KEY, TO_KEY);

        assertThatJson(resultString).node(METHOD_KEY).isEqualTo(command.getMethod().toString().toLowerCase());

        assertThatJson(resultString).node(RESOURCE_KEY).isPresent();
        assertThatJson(resultString).node(TYPE_KEY).isEqualTo(command.getResource().getMediaType().toString());

        assertThatJson(resultString).node(RESOURCE_KEY + "." + FULL_NAME_KEY).isEqualTo(resource.getFullName());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + PHOTO_URI_KEY).isEqualTo(resource.getPhotoUri().toString());

        assertThatJson(resultString).node(STATUS_KEY).isAbsent();
        assertThatJson(resultString).node(REASON_KEY).isAbsent();
    }

    @Test
    public void serialize_RosterResponseCommand_ReturnsValidJsonString()
    {
        DocumentCollection resource = createRoster();
        Command command = createCommand(resource);
        command.setPp(createNode());
        command.setMethod(Command.CommandMethod.GET);
        command.setStatus(Command.CommandStatus.SUCCESS);

        String metadataKey1 = "randomString1";
        String metadataValue1 = createRandomString(50);
        String metadataKey2 = "randomString2";
        String metadataValue2 = createRandomString(50);
        Map<String, String> metadata = new HashMap<>();
        metadata.put(metadataKey1, metadataValue1);
        metadata.put(metadataKey2, metadataValue2);
        command.setMetadata(metadata);

        String resultString = target.serialize(command);

        assertJsonEnvelopeProperties(command, resultString, ID_KEY, FROM_KEY, PP_KEY, TO_KEY, METADATA_KEY);

        assertThatJson(resultString).node(METHOD_KEY).isEqualTo(command.getMethod().toString().toLowerCase());

        assertThatJson(resultString).node(RESOURCE_KEY).isPresent();
        assertThatJson(resultString).node(TYPE_KEY).isEqualTo(command.getResource().getMediaType().toString());

        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY).isPresent();

        Document[] contacts = resource.getItems();

        Contact contact = (Contact)contacts[0];
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[0]."+ IDENTITY_KEY).isEqualTo(contact.getIdentity());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[0]."+ NAME_KEY).isEqualTo(contact.getName());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[0]."+ IS_PENDING_KEY).isEqualTo(contact.getIsPending());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[0]."+ SHARE_ACCOUNT_INFO_KEY).isEqualTo(contact.getShareAccountInfo());

        contact = (Contact)contacts[1];
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[1]."+ IDENTITY_KEY).isEqualTo(contact.getIdentity());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[1]." + NAME_KEY).isEqualTo(contact.getName());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[1]."+ IS_PENDING_KEY).isEqualTo(contact.getIsPending());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[1]."+ SHARE_ACCOUNT_INFO_KEY).isEqualTo(contact.getShareAccountInfo());

        contact = (Contact)contacts[2];
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[2]." + IDENTITY_KEY).isEqualTo(contact.getIdentity());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[2]." + NAME_KEY).isEqualTo(contact.getName());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[2]."+ IS_PENDING_KEY).isEqualTo(contact.getIsPending());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[2]."+ SHARE_ACCOUNT_INFO_KEY).isEqualTo(contact.getShareAccountInfo());

        assertThatJson(resultString).node(STATUS_KEY).isPresent();
        assertThatJson(resultString).node(REASON_KEY).isAbsent();
    }

    @Test
    public void serialize_ContactCollectionResponseCommand_ReturnsValidJsonString()
    {
        Contact contact1 = createContact();
        contact1.setShareAccountInfo(true);
        contact1.setSharePresence(true);

        Contact contact2 = createContact();
        contact1.setShareAccountInfo(true);

        Contact contact3 = createContact();

        DocumentCollection resource = createDocumentCollection(contact1, contact2, contact3);

        Command command = createCommand(resource);
        command.setPp(createNode());
        command.setMethod(Command.CommandMethod.GET);
        command.setStatus(Command.CommandStatus.SUCCESS);

        String metadataKey1 = "randomString1";
        String metadataValue1 = createRandomString(50);
        String metadataKey2 = "randomString2";
        String metadataValue2 = createRandomString(50);

        Map<String, String> metadata = new HashMap<>();
        metadata.put(metadataKey1, metadataValue1);
        metadata.put(metadataKey2, metadataValue2);
        command.setMetadata(metadata);

        String resultString = target.serialize(command);

        assertJsonEnvelopeProperties(command, resultString, ID_KEY, FROM_KEY, PP_KEY, TO_KEY, METADATA_KEY);

        assertThatJson(resultString).node(METHOD_KEY).isEqualTo(command.getMethod().toString().toLowerCase());
        assertThatJson(resultString).node(TYPE_KEY).isEqualTo(command.getResource().getMediaType().toString());

        assertThatJson(resultString).node(RESOURCE_KEY).isPresent();

        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY).isPresent().isArray().ofLength(3);

        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEM_TYPE_KEY).isEqualTo(contact1.getMediaType().toString());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + TOTAL_KEY).isEqualTo(resource.getTotal());

        assertThatJson(resultString).node(STATUS_KEY).isPresent();
        assertThatJson(resultString).node(REASON_KEY).isAbsent();
    }

    //endregion Command

    //endregion serialize method

    //region deserialize method

    //region Message

    @Test
    public void deserialize_TextMessage_ReturnsValidInstance()
    {
        UUID id = UUID.randomUUID();
        Node from = createNode();
        Node pp = createNode();
        Node to = createNode();

        String randomKey1 = "randomString1";
        String randomKey2 = "randomString2";
        String randomString1 = createRandomString(50);
        String randomString2 = createRandomString(50);

        String text = createRandomString(50);

        String json = StringUtils.format(
                "{\"type\":\"text/plain\",\"content\":\"{0}\",\"id\":\"{1}\",\"from\":\"{2}\",\"pp\":\"{3}\",\"to\":\"{4}\",\"metadata\":{\"{5}\":\"{6}\",\"{7}\":\"{8}\"}}",
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

        assertTrue(message.getContent() instanceof PlainText);

        PlainText textContent = (PlainText)message.getContent();
        assertEquals(text, textContent.getText());
    }

    @Test
    public void deserialize_ChatStateMessage_ReturnsValidInstance()
    {
        UUID id = UUID.randomUUID();
        Node from = createNode();
        Node pp = createNode();
        Node to = createNode();

        String randomKey1 = "randomString1";
        String randomKey2 = "randomString2";
        String randomString1 = createRandomString(50);
        String randomString2 = createRandomString(50);

        ChatState.ChatStateEvent state = ChatState.ChatStateEvent.DELETING;

        String json = StringUtils.format(
                "{\"type\":\"application/vnd.lime.chatstate+json\",\"content\":{\"state\":\"{0}\"},\"id\":\"{1}\",\"from\":\"{2}\",\"pp\":\"{3}\",\"to\":\"{4}\",\"metadata\":{\"{5}\":\"{6}\",\"{7}\":\"{8}\"}}",
                StringUtils.toCamelCase(state.toString()),
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


        assertTrue(message.getContent() instanceof ChatState);

        ChatState textContent = (ChatState)message.getContent();
        assertEquals(state, textContent.getState());
    }

    @Test
    public void deserialize_FireAndForgetTextMessage_ReturnsValidInstance()
    {
        Node from = createNode();
        Node to = createNode();

        String text = createRandomString(50);

        String json = StringUtils.format(
                "{\"type\":\"text/plain\",\"content\":\"{0}\",\"from\":\"{1}\",\"to\":\"{2}\"}",
                text,
                from,
                to
        );

        Envelope envelope = target.deserialize(json);

        assertTrue(envelope instanceof Message);

        Message message = (Message)envelope;

        assertEquals(from, message.getFrom());
        assertEquals(to, message.getTo());

        assertEquals(message.getId(), null);

        assertNull(message.getPp());
        assertNull(message.getMetadata());

        assertTrue(message.getContent() instanceof PlainText);
        PlainText textContent = (PlainText)message.getContent();
        assertEquals(text, textContent.getText());
    }

    @Test
    public void deserialize_FireAndForgetChatStateMessage_ReturnsValidInstance()
    {
        Node from = createNode();
        Node to = createNode();

        ChatState.ChatStateEvent state = ChatState.ChatStateEvent.COMPOSING;

        String json = StringUtils.format(
                "{\"type\":\"application/vnd.lime.chatstate+json\",\"content\":{\"state\":\"{0}\"},\"from\":\"{1}\",\"to\":\"{2}\"}",
                StringUtils.toCamelCase(state.toString()),
                from,
                to
        );

        Envelope envelope = target.deserialize(json);

        Message message = (Message)envelope;

        assertEquals(from, message.getFrom());
        assertEquals(to, message.getTo());

        assertEquals(message.getId(), null);

        assertNull(message.getPp());
        assertNull(message.getMetadata());

        assertTrue(message.getContent() instanceof ChatState);
        ChatState textContent = (ChatState)message.getContent();
        assertEquals(state, textContent.getState());
    }

    //endregion Message

    //region Command

    @Test
    public void deserialize_ReceiptRequestCommand_ReturnsValidInstance() {
        // Arrange
        Command.CommandMethod method = SET;
        UUID id = UUID.randomUUID();

        String json = StringUtils.format(
                "{\"type\":\"application/vnd.lime.receipt+json\",\"resource\":{\"events\":[\"dispatched\",\"received\"]},\"method\":\"{0}\",\"id\":\"{1}\"}",
                StringUtils.toCamelCase(method.toString()),
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
        assertThat(receipt.getEvents()).containsOnly(Event.DISPATCHED, Event.RECEIVED);

        assertThat(command.getUri()).isNull();
    }

    @Test
    public void deserialize_AccountRequestCommand_ReturnsValidInstance() throws URISyntaxException {
        // Arrange
        Command.CommandMethod method = GET;
        UUID id = UUID.randomUUID();
        String fullName = createRandomString(30);
        URI photoUri = createUri("http", 80);

        String json = StringUtils.format(
                "{\"type\":\"{0}\",\"resource\":{\"fullName\":\"{1}\",\"photoUri\":\"{2}\"},\"method\":\"{3}\",\"id\":\"{4}\"}",
                Account.MIME_TYPE,
                fullName,
                photoUri,
                StringUtils.toCamelCase(method.toString()),
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

        assertThat(command.getType().toString()).isEqualTo(Account.MIME_TYPE);
        assertThat(command.getResource()).isNotNull().isInstanceOf(Account.class);
        Account account = (Account) command.getResource();
        assertThat(account.getFullName()).isEqualTo(fullName);
        assertThat(account.getPhotoUri()).isEqualTo(photoUri);

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

        Command.CommandMethod method = Command.CommandMethod.GET;

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
        assertThat(contacts[0].getIsPending()).isNotNull().isTrue();
        assertThat(contacts[0].getShareAccountInfo()).isNotNull().isFalse();
        assertThat(contacts[0].getSharePresence()).isNull();

        assertThat(contacts[1].getIdentity()).isEqualTo(identity2);
        assertThat(contacts[1].getName()).isEqualTo(name2);
        assertThat(contacts[1].getIsPending()).isNull();
        assertThat(contacts[1].getShareAccountInfo()).isNull();
        assertThat(contacts[1].getSharePresence()).isNotNull().isFalse();

        assertThat(contacts[2].getIdentity()).isEqualTo(identity3);
        assertThat(contacts[2].getName()).isEqualTo(name3);
        assertThat(contacts[2].getIsPending()).isNotNull().isTrue();
        assertThat(contacts[2].getShareAccountInfo()).isNull();
        assertThat(contacts[2].getSharePresence()).isNotNull().isFalse();
    }

    @Test
    public void deserialize_EmptyContactCollectionResponseCommandWithMissingTotal_ReturnsValidInstance() {
        // Arrange
        Command.CommandMethod method = Command.CommandMethod.GET;

        UUID id = UUID.randomUUID();
        Node from = createNode();
        Node  pp = createNode();
        Node to = createNode();

        String json = StringUtils.format(
                "{\"type\":\"application/vnd.lime.collection+json\",\"resource\":{\"itemType\":\"application/vnd.lime.contact+json\",\"items\":[]},\"method\":\"get\",\"status\":\"success\",\"id\":\"{0}\",\"from\":\"{1}\",\"pp\":\"{2}\",\"to\":\"{3}\"}",
                id,
                from,
                pp,
                to);

        // Act
        Envelope envelope = target.deserialize(json);

        assertThat(envelope).isInstanceOf(Command.class);

        Command command = (Command)envelope;

        assertThat(command.getId()).isEqualTo(id);
        assertThat(command.getFrom()).isEqualTo(from);
        assertThat(command.getTo()).isEqualTo(to);
        assertThat(command.getPp()).isEqualTo(pp);
        assertThat(command.getMetadata()).isNull();

        assertThat(command.getMethod()).isEqualTo(method);

        assertThat(command.getType().toString()).isEqualTo(DocumentCollection.MIME_TYPE);
        assertThat(command.getResource()).isNotNull().isInstanceOf(DocumentCollection.class);

        DocumentCollection documents = (DocumentCollection)command.getResource();
        assertThat(documents.getTotal()).isEqualTo(0);

        Document[] items = documents.getItems();
        assertThat(items).isNotNull().hasSize(0);
    }

    //endregion Command

    //endregion deserialize method
}