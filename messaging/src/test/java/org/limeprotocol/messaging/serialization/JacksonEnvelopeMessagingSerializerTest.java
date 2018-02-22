package org.limeprotocol.messaging.serialization;

import net.take.iris.messaging.resources.Schedule;
import net.take.iris.messaging.resources.artificialIntelligence.AnalysisResponse;
import net.take.iris.messaging.resources.artificialIntelligence.Entity;
import net.take.iris.messaging.resources.artificialIntelligence.Intention;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.limeprotocol.*;
import org.limeprotocol.messaging.Registrator;
import org.limeprotocol.messaging.contents.*;
import org.limeprotocol.messaging.resources.Account;
import org.limeprotocol.messaging.resources.Capability;
import org.limeprotocol.messaging.resources.Contact;
import org.limeprotocol.messaging.resources.Receipt;
import org.limeprotocol.messaging.testHelpers.MessagingJsonConstants;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;
import org.limeprotocol.testHelpers.Dummy;
import org.limeprotocol.testHelpers.JsonConstants;
import org.limeprotocol.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
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
import static org.limeprotocol.testHelpers.JsonConstants.Message.CONTENT_KEY;

public class JacksonEnvelopeMessagingSerializerTest {

    private JacksonEnvelopeSerializer target;

    @Before
    public void setUp() throws Exception {
        Registrator.registerDocuments();
        net.take.iris.messaging.Registrator.registerDocuments();
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

        assertThatJson(resultString).node(CONTENT_KEY).isEqualTo(content.getText());
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
        assertThatJson(resultString).node(CONTENT_KEY).isEqualTo(content.getText());
    }

    @Test
    public void serialize_selectMessage_returnsValidJsonString() {
        // Arrange
        Select select = createSelect();
        Message message = Dummy.createMessage(select);

        // Act
        String resultString = target.serialize(message);

        // Assert
        assertJsonEnvelopeProperties(message, resultString, ID_KEY, FROM_KEY, TO_KEY);
    }

    @Test
    public void serialize_documentSelectMessage_returnsValidJsonString() {
        // Arrange
        DocumentSelect select = createDocumentSelect();
        Message message = Dummy.createMessage(select);

        // Act
        String resultString = target.serialize(message);

        // Assert
        assertJsonEnvelopeProperties(message, resultString, ID_KEY, FROM_KEY, TO_KEY);
    }

    @Test
    public void serialize_weblinkMessage_returnsValidJsonString() {
        // Arrange
        URI uri = URI.create("http://dummy.domain.com:785/file%20name.mp3");
        WebLink webLink = createWebLink(uri);
        Message message = Dummy.createMessage(webLink);

        // Act
        String resultString = target.serialize(message);

        // Assert
        assertJsonEnvelopeProperties(message, resultString, ID_KEY,  FROM_KEY, TO_KEY);
        assertThatJson(resultString).node(TYPE_KEY).isEqualTo(message.getType().toString());
        assertThatJson(resultString).node(CONTENT_KEY + "." + "uri").isEqualTo(uri);
        assertThatJson(resultString).node(CONTENT_KEY + "." + "text").isEqualTo(webLink.getText());
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
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[0]."+ IDENTITY_KEY).isEqualTo(contact.getIdentity().toString());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[0]."+ NAME_KEY).isEqualTo(contact.getName());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[0]."+ IS_PENDING_KEY).isEqualTo(contact.getIsPending());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[0]."+ SHARE_ACCOUNT_INFO_KEY).isEqualTo(contact.getShareAccountInfo());

        contact = (Contact)contacts[1];
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[1]."+ IDENTITY_KEY).isEqualTo(contact.getIdentity().toString());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[1]." + NAME_KEY).isEqualTo(contact.getName());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[1]."+ IS_PENDING_KEY).isEqualTo(contact.getIsPending());
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[1]."+ SHARE_ACCOUNT_INFO_KEY).isEqualTo(contact.getShareAccountInfo());

        contact = (Contact)contacts[2];
        assertThatJson(resultString).node(RESOURCE_KEY + "." + ITEMS_KEY + "[2]." + IDENTITY_KEY).isEqualTo(contact.getIdentity().toString());
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


    @Test
    public void serialize_Scheduled_ReturnsValidJsonString() throws ParseException {

        String dateString = "2018-01-30 13:00:00";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Schedule schedule = new Schedule();
        schedule.setWhen(df.parse(dateString));

        Command command = createCommand(schedule);
        command.setMethod(Command.CommandMethod.SET);

        String resultString = target.serialize(command);

        assertThatJson(resultString).node(METHOD_KEY).isEqualTo(command.getMethod().toString().toLowerCase());
        assertThatJson(resultString).node(TYPE_KEY).isEqualTo(command.getResource().getMediaType().toString());
        assertThatJson(resultString).node(RESOURCE_KEY).isPresent();
        assertThatJson(resultString).node(RESOURCE_KEY + ".when" ).isEqualTo("2018-01-30T13:00:00Z");
    }

    //endregion Command

    //endregion serialize method

    //region deserialize method

    //region Message

    @Test
    public void deserialize_TextMessage_ReturnsValidInstance()
    {
        String id = EnvelopeId.newId();
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
        String id = EnvelopeId.newId();
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

    @Test
    public void deserialize_documentContainerDocumentCollectionMessage_returnsValidInstance() {

        // Arrange
        String id = EnvelopeId.newId();
        String json = StringUtils.format(
                "{\"type\":\"application/vnd.lime.collection+json\",\"content\":{\"total\":4,\"itemType\":\"application/vnd.lime.container+json\",\"items\":[{\"type\":\"text/plain\",\"value\":\"text1\"},{\"type\":\"application/vnd.lime.account+json\",\"value\":{\"fullName\":\"My Name\",\"photoUri\":\"http://url.com/resource\"}},{\"type\":\"application/q9gn1nsz6y+json\",\"value\":{\"o4s9txn80q\":\"}2['\u00F23 /bdkc]\u00FA2,\u00BA &%f0j\u00F9u#\u00F2\u00FA9;\u00EC\\\"t}#\u00F3(\u00E9a_94\u00E00q5m==\\\\\",\"ynpinmi0oq\":20,\"dkker2borf\":\"2016-04-13T16:24:49.729Z\",\"e98cyp215l\":{\"ljwbthakfx\":\"\\\\@(m1g=q.-jql[)5#n,\u00E09\u00BA\u00A8kg~]t(x:<u\u00E1z'8?.-^_cvqkk\u00EC@n\",\"z4uih47pct\":19,\"nxp3n8km78\":\"2016-04-13T16:24:49.729Z\"},\"sinvm70xls\":[{\"ypdd57j78y\":\"<>5_\u00ECnb'!,b.ps8\u00EC=9\\\\o\\\\_*qc6#k0\u00E8]$j\u00E1=-u\u00E1\u00FAq\u00EC{\u00E0r\u00F2\u00BAt\u00ED[\u00EC\",\"l3d24gigtt\":34,\"5ltasvmv3y\":\"2016-04-13T16:24:49.729Z\"},{\"1twigyljcf\":\"=!6-\u00F360 94fy2\u00A8e23q72\u00E0v\u00E9t(u!&[%\u00FA\u00E8#4f7\u00E0\u00ECkjv2n9=@pjp~\",\"ke4zjmvfbw\":46,\"2l7rf39qwq\":\"2016-04-13T16:24:49.729Z\"},{\"pckdtdowdc\":\"11\u00E9q>e:j,^;\u00F3\u00A8o@cs\u00F9@'r}(3\u00EDe(=,uq*\u00F9(+!!..hd\u00E9;~.*(j=\u00A8\",\"5pfq4y1rmz\":24,\"foqvh78vau\":\"2016-04-13T16:24:49.729Z\"}]}},{\"type\":\"vxhfxfm3tz/hhnzgm4kmh\",\"value\":\"9nav5pkhswvsw7mh24r1b3agbgic43piylveh1z6xtfz77nibt\"}]},\"id\":\"{0}\",\"from\":\"9afudsyl@je29bkh1bs.com/yq1oh\",\"to\":\"9zpfpsuc@d63uusxbfq.com/btp7i\"}",
                id);

        // Act
        Envelope envelope = target.deserialize(json);

        // Assert
        assertEquals(id, envelope.getId());
        assertTrue(envelope instanceof Message);
        Message message = (Message)envelope;
        assertTrue(message.getContent() instanceof DocumentCollection);
        DocumentCollection documentCollection = (DocumentCollection)message.getContent();
        assertEquals(4, documentCollection.getTotal());
        assertEquals(MediaType.parse(DocumentContainer.MIME_TYPE), documentCollection.getItemType());
        assertNotNull(documentCollection.getItems());
        assertEquals(4, documentCollection.getItems().length);
        assertTrue(documentCollection.getItems()[0] instanceof DocumentContainer);
        DocumentContainer container1 = (DocumentContainer)documentCollection.getItems()[0];
        assertTrue(documentCollection.getItems()[1] instanceof DocumentContainer);
        DocumentContainer container2 = (DocumentContainer)documentCollection.getItems()[1];
        assertTrue(documentCollection.getItems()[2] instanceof DocumentContainer);
        DocumentContainer container3 = (DocumentContainer)documentCollection.getItems()[2];
        assertTrue(documentCollection.getItems()[3] instanceof DocumentContainer);
        DocumentContainer container4 = (DocumentContainer)documentCollection.getItems()[3];
        assertEquals(MediaType.parse(PlainText.MIME_TYPE), container1.getType());
        assertTrue(container1.getValue() instanceof PlainText);
        PlainText document1 = (PlainText)container1.getValue();
        assertEquals("text1", document1.getText());
        assertEquals(MediaType.parse(Account.MIME_TYPE), container2.getType());
        assertTrue(container2.getValue() instanceof Account);
        Account document2 = (Account)container2.getValue();
        assertEquals("My Name", document2.getFullName());
        Assert.assertNotNull(document2.getPhotoUri());
        assertEquals("http://url.com/resource", document2.getPhotoUri().toString());
        assertEquals(MediaType.parse("application/q9gn1nsz6y+json"), container3.getType());
        assertTrue(container3.getValue() instanceof JsonDocument);
        JsonDocument document3 = (JsonDocument)container3.getValue();
        Assert.assertTrue(document3.size() > 0);
        assertEquals(MediaType.parse("vxhfxfm3tz/hhnzgm4kmh"), container4.getType());
        assertTrue(container3.getValue() instanceof JsonDocument);
        PlainDocument document4 = (PlainDocument)container4.getValue();
        assertEquals("9nav5pkhswvsw7mh24r1b3agbgic43piylveh1z6xtfz77nibt", document4.getValue());
    }

    @Test
    public void deserialize_selectMessage_returnValidInstance() {
        // Arrange
        String json = "{\"id\":\"9c18cf8a-6270-4670-8900-02f5751f4366\",\"from\":\"clmmeemi@z14wp95v5a.com/6hjtk\",\"to\":\"web9wo48@ude6wpmltb.com/imo3y\",\"content\":{\"text\":\"g0m585e4hrd11um88mh6ketg6tv8uj3a8jfzcuj4hvwxssu48iqtqam9ha71w2cl46c1lhqa6wq2drlb91x3um7mk9a4009ioiij\",\"options\":[{\"order\":1,\"text\":\"qviwrj95mc\",\"type\":\"text/plain\",\"value\":\"aqgew853rlhgajofzgnp6ij0v8xym1xq56pdtm6grkd5iebk9tzpyehfv8gjab2ng9gaujx2npcyqfy64wes4ar5rqodzyb3g0lm97jx6eh629biok5gfqhlq3okn62w73bz8xac4sahm1ccwjvse6\"},{\"order\":2,\"text\":\"vtxfmwc0yk\"},{\"text\":\"1a7jmddoyf\"},{\"type\":\"application/6md3v82upq+json\",\"value\":{\"c61ozm28zj\":11,\"xr2tovgzmt\":\"bviy3y4io3wwmp5wmkpl87z9uhgcxd4lk5dbd8mo982vgsg8el\"}}]},\"type\":\"application/vnd.lime.select+json\",\"sender\":\"clmmeemi@z14wp95v5a.com/6hjtk\"}";

        // Act
        Envelope envelope = target.deserialize(json);

        // Assert
        assertEquals("9c18cf8a-6270-4670-8900-02f5751f4366", envelope.getId());
        assertEquals("clmmeemi@z14wp95v5a.com/6hjtk", envelope.getFrom().toString());
        assertEquals("web9wo48@ude6wpmltb.com/imo3y", envelope.getTo().toString());
        assertTrue(envelope instanceof Message);
        Message message = (Message)envelope;
        assertTrue(message.getContent() instanceof Select);
        Select select = (Select)message.getContent();
        assertEquals(select.getText(), "g0m585e4hrd11um88mh6ketg6tv8uj3a8jfzcuj4hvwxssu48iqtqam9ha71w2cl46c1lhqa6wq2drlb91x3um7mk9a4009ioiij");
        assertNotNull(select.getOptions());
        assertEquals(select.getOptions().length, 4);

        assertEquals(select.getOptions()[0].getOrder(), (Integer)1);
        assertEquals(select.getOptions()[0].getText(), "qviwrj95mc");
        assertEquals(select.getOptions()[0].getType().toString(), "text/plain");
        assertEquals(select.getOptions()[0].getValue().toString(), "aqgew853rlhgajofzgnp6ij0v8xym1xq56pdtm6grkd5iebk9tzpyehfv8gjab2ng9gaujx2npcyqfy64wes4ar5rqodzyb3g0lm97jx6eh629biok5gfqhlq3okn62w73bz8xac4sahm1ccwjvse6");

        assertEquals(select.getOptions()[1].getOrder(), (Integer)2);
        assertEquals(select.getOptions()[1].getText(), "vtxfmwc0yk");
        assertNull(select.getOptions()[1].getType());
        assertNull(select.getOptions()[1].getValue());

        assertNull(select.getOptions()[2].getOrder());
        assertEquals(select.getOptions()[2].getText(), "1a7jmddoyf");
        assertNull(select.getOptions()[2].getType());
        assertNull(select.getOptions()[2].getValue());

        assertNull(select.getOptions()[3].getOrder());
        assertNull(select.getOptions()[3].getText());
        assertEquals(select.getOptions()[3].getType().toString(), "application/6md3v82upq+json");

        assertTrue(select.getOptions()[3].getValue() instanceof JsonDocument);
        JsonDocument jsonDocument = (JsonDocument)(select.getOptions()[3].getValue());
        assertTrue(jsonDocument.containsKey("c61ozm28zj"));
        assertEquals(jsonDocument.get("c61ozm28zj"), 11);
        assertTrue(jsonDocument.containsKey("xr2tovgzmt"));
        assertEquals(jsonDocument.get("xr2tovgzmt"), "bviy3y4io3wwmp5wmkpl87z9uhgcxd4lk5dbd8mo982vgsg8el");
    }

    @Test
    public void deserialize_documentSelectMessage_returnValidInstance() {
        // Arrange
        String json = "{\"id\":\"message-id\",\"from\":\"andreb@msging.net\",\"type\":\"application/vnd.lime.document-select+json\",\"content\":{\"header\":{\"type\":\"application/vnd.lime.media-link+json\",\"value\":{\"title\":\"Welcome to Peter\'s Hats\",\"text\":\"We\'ve got the right hat for everyone.\",\"type\":\"image/jpeg\",\"uri\":\"http://petersapparel.parseapp.com/img/item100-thumb.png\"}},\"options\":[{\"label\":{\"type\":\"application/vnd.lime.web-link+json\",\"value\":{\"text\":\"View Website\",\"uri\":\"https://petersapparel.parseapp.com/view_item?item_id=100\"}}},{\"label\":{\"type\":\"text/plain\",\"value\":\"Start Chatting\"},\"value\":{\"type\":\"application/json\",\"value\":{\"key\":\"key1\",\"value\":1}}}]}}";

        // Act
        Envelope envelope = target.deserialize(json);

        // Assert
        assertNotNull(envelope);
        assertEquals("message-id", envelope.getId());
        assertEquals("andreb@msging.net", envelope.getFrom().toString());
        assertNull(envelope.getTo());
        assertTrue(envelope instanceof Message);
        Message message = (Message)envelope;
        assertTrue(message.getContent() instanceof DocumentSelect);
        DocumentSelect documentSelect = (DocumentSelect)message.getContent();
        assertNotNull(documentSelect.getHeader());
        Assert.assertTrue(documentSelect.getHeader().getValue() instanceof MediaLink);
        MediaLink header = (MediaLink)documentSelect.getHeader().getValue();
        assertEquals("Welcome to Peter's Hats", header.getTitle());
        assertEquals("We've got the right hat for everyone.", header.getText());
        assertEquals("image/jpeg", header.getType().toString());
        assertEquals("http://petersapparel.parseapp.com/img/item100-thumb.png", header.getUri().toString());
        assertNotNull(documentSelect.getOptions());
        assertEquals(2, documentSelect.getOptions().length);
        assertTrue(documentSelect.getOptions()[0].getLabel().getValue() instanceof WebLink);
        WebLink option1Label = (WebLink)documentSelect.getOptions()[0].getLabel().getValue();
        assertEquals("View Website", option1Label.getText());
        assertEquals("https://petersapparel.parseapp.com/view_item?item_id=100", option1Label.getUri().toString());
        assertNull(documentSelect.getOptions()[0].getValue());
        assertTrue(documentSelect.getOptions()[1].getLabel().getValue() instanceof PlainText);
        PlainText option2Label = (PlainText)documentSelect.getOptions()[1].getLabel().getValue();
        assertEquals("Start Chatting", option2Label.getText());
        assertTrue(documentSelect.getOptions()[1].getValue().getValue() instanceof JsonDocument);
        JsonDocument option2Value = (JsonDocument)documentSelect.getOptions()[1].getValue().getValue();
        assertTrue(option2Value.containsKey("key"));
        assertEquals("key1", option2Value.get("key"));
        assertTrue(option2Value.containsKey("value"));
        assertEquals(1, option2Value.get("value"));
    }

    @Test
    public void deserialize_WeblinkMessage_ReturnsValidInstance()
    {
        // Arrange
        String json =
                "{\"type\":\"application/vnd.lime.web-link+json\",\"content\":{\"uri\":\"http://e0x0rkuaof.com:9288/file%20name.jpg\",\"previewUri\":\"http://pcmcjxomhd.com:9875/\",\"previewType\":\"image/jpeg\",\"text\":\"b9s38pra6s7w7b4w1jca6lzf9zp8927ciy4lwdsa3y1gc2ekiw\"},\"id\":\"25058656-ea3e-4f2a-9b27-fe14d1470796\",\"from\":\"6fjghzjm@3j9saev4nj.com/gtax0\",\"to\":\"cghusdgu@f0m512bqfb.com/jjjak\"}";

        // Act
        Envelope envelope = target.deserialize(json);

        // Assert
        assertTrue(envelope instanceof Message);
        Message message = (Message)envelope;
        assertTrue(message.getContent() instanceof WebLink);
        WebLink webLink = (WebLink) message.getContent();
        assertNotNull(webLink.getUri());
        assertEquals(webLink.getUri().toString(), "http://e0x0rkuaof.com:9288/file%20name.jpg");
        assertNotNull(webLink.getPreviewUri());
        assertEquals(webLink.getPreviewUri().toString(), "http://pcmcjxomhd.com:9875/");
        assertNotNull(webLink.getPreviewType());
        assertEquals(webLink.getPreviewType().toString(), "image/jpeg");
        assertEquals(webLink.getText(), "b9s38pra6s7w7b4w1jca6lzf9zp8927ciy4lwdsa3y1gc2ekiw");
    }

    //endregion Message

    //region Command

    @Test
    public void deserialize_ReceiptRequestCommand_ReturnsValidInstance() {
        // Arrange
        Command.CommandMethod method = SET;
        String id = EnvelopeId.newId();

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
        String id = EnvelopeId.newId();
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

        String id = EnvelopeId.newId();
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

        String id = EnvelopeId.newId();
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

        // Assert
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

    @Test
    public void deserialize_IntentionCollectionCommand_ReturnValidInstance() {

        // Arrange
        String json = "{\"type\":\"application/vnd.lime.collection+json\",\"resource\":{\"total\":7,\"itemType\":\"application/vnd.iris.ai.intention+json\",\"items\":[{\"id\":\"order_pizza2\",\"name\":\"Order pizza2\",\"storageDate\":\"2017-12-29T18:28:56.800Z\"},{\"id\":\"teste_i7\",\"name\":\"Teste I7\",\"storageDate\":\"2017-12-29T14:29:42.340Z\"},{\"id\":\"teste_i4\",\"name\":\"Teste I4\",\"storageDate\":\"2017-12-29T14:22:34.470Z\"},{\"id\":\"teste_i3\",\"name\":\"Teste I3\",\"storageDate\":\"2017-12-29T12:55:09.910Z\"},{\"id\":\"teste_i2\",\"name\":\"Teste I2\",\"storageDate\":\"2017-12-28T16:52:11.080Z\"},{\"id\":\"xpto\",\"name\":\"xpto\",\"storageDate\":\"2017-12-28T16:06:01.540Z\"},{\"id\":\"none\",\"name\":\"Resposta padrão\",\"storageDate\":\"2017-12-28T16:05:34.670Z\"}]},\"method\":\"get\",\"status\":\"success\",\"id\":\"1291b3c0-756f-4abd-9c8e-e9a4de90608b\",\"from\":\"postmaster@ai.msging.net/#irismsging2\",\"pp\":\"postmaster@ai.msging.net/#irismsging2-1\",\"to\":\"botwh@msging.net\"}";

        // Act
        Envelope envelope = target.deserialize(json);

        // Assert
        assertThat(envelope).isInstanceOf(Command.class);
        Command command = (Command)envelope;
        assertThat(command.getResource()).isNotNull().isInstanceOf(DocumentCollection.class);

        DocumentCollection documents = (DocumentCollection)command.getResource();
        assertThat(documents.getTotal()).isEqualTo(7);
        assertThat(documents.getItems().length).isEqualTo(7);

        for (Document document : documents.getItems()) {
            assertThat(document).isInstanceOf(Intention.class);
            Intention intention = (Intention)document;
            assertThat(intention.getId()).isNotNull();
            assertThat(intention.getName()).isNotNull();
            assertThat(intention.getStorageDate()).isNotNull();
        }
    }

    @Test
    public void deserialize_AIAnalysisResponseCommand_ReturnValidInstance() {

        // Arrange
        String json = "{\n" +
                "  \"id\": \"9\",\n" +
                "  \"from\": \"postmaster@ai.msging.net/#irismsging1\",\n" +
                "  \"to\": \"contact@msging.net/default\",\n" +
                "  \"method\": \"set\",\n" +
                "  \"status\": \"success\",\n" +
                "  \"type\": \"application/vnd.iris.ai.analysis-response+json\",\n" +
                "  \"resource\": {\n" +
                "    \"text\":\"I want a pepperoni pizza\",\n" +
                "    \"intentions\": [\n" +
                "      {\n" +
                "        \"id\":\"order_pizza\",\n" +
                "        \"name\":\"Order pizza\",\n" +
                "        \"score\": 0.95\n" +
                "      }\n" +
                "    ],\n" +
                "    \"entities\":  [\n" +
                "      {\n" +
                "        \"id\":\"flavor\",\n" +
                "        \"name\":\"Flavor\",\n" +
                "        \"value\":\"Pepperoni\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        // Act
        Envelope envelope = target.deserialize(json);

        // Assert
        assertThat(envelope).isInstanceOf(Command.class);
        Command command = (Command)envelope;
        assertThat(command.getResource()).isNotNull().isInstanceOf(AnalysisResponse.class);

        AnalysisResponse analysisResponse = (AnalysisResponse)command.getResource();
        assertThat(analysisResponse.getText()).isNotNull().isNotEmpty();
        assertThat(analysisResponse.getEntities().length).isEqualTo(1);
        assertThat(analysisResponse.getIntentions().length).isEqualTo(1);
    }

    @Test
    public void deserialize_ResourceCollectionCommand_ReturnValidInstance() {

        // Arrange
        String json = "{\"type\":\"application/vnd.lime.collection+json\",\"resource\":{\"total\":5,\"itemType\":\"text/plain\",\"items\":[\"Teste_Content2-Entity_Content2\",\"Teste_Content3-Entity_Content3\",\"Teste_Content4Entity_Content4\",\"Teste_Content6::Entity_Content6\",\"Teste_ContentEntity_Content\"]},\"method\":\"get\",\"status\":\"success\",\"id\":\"830ad17d-875f-4c0a-b7a3-f08def17f633\",\"from\":\"postmaster@msging.net/#irismsging3\",\"to\":\"botwh@msging.net\"}";

        // Act
        Envelope envelope = target.deserialize(json);

        // Assert
        assertThat(envelope).isInstanceOf(Command.class);
        Command command = (Command)envelope;
        assertThat(command.getResource()).isNotNull().isInstanceOf(DocumentCollection.class);

        DocumentCollection documents = (DocumentCollection)command.getResource();
        assertThat(documents.getTotal()).isEqualTo(5);
        assertThat(documents.getItems().length).isEqualTo(5);

        for (Document document : documents.getItems()) {
            assertThat(document).isInstanceOf(PlainDocument.class);
            PlainDocument plainDocument = (PlainDocument) document;
            assertThat(plainDocument.getValue()).isNotNull();
            assertThat(plainDocument.getMediaType().toString()).isEqualTo("text/plain");
        }
    }

    @Test
    public void deserialize_EntityResponseCommand_ReturValidInstance() {

        // Arrange
        String json = "{\"type\":\"application/vnd.iris.ai.entity+json\",\"resource\":{\"id\":\"e\",\"name\":\"e\",\"storageDate\":\"2018-02-20T18:13:26.940Z\",\"values\":[{\"name\":\"educacao \",\"synonymous\":[]},{\"name\":\"eletronico\",\"synonymous\":[\"eletronica\",\"eletronica\",\"eletronicos\"]},{\"name\":\"elite\",\"synonymous\":[]},{\"name\":\"emissor\",\"synonymous\":[]},{\"name\":\"empresa\",\"synonymous\":[\"empresas\",\"empresarial\"]},{\"name\":\"emprestimo\",\"synonymous\":[\"emprestimos\"]},{\"name\":\"erro\",\"synonymous\":[\"erros\",\"problema\",\"defeito\",\"falha\"]},{\"name\":\"esfera\",\"synonymous\":[]},{\"name\":\"estrangeira\",\"synonymous\":[]},{\"name\":\"estudante\",\"synonymous\":[\"estudantes\"]},{\"name\":\"euro\",\"synonymous\":[]},{\"name\":\"excecutiva\",\"synonymous\":[]},{\"name\":\"experiencia\",\"synonymous\":[\"experiencia\",\"experience\"]},{\"name\":\"expiracao\",\"synonymous\":[\"venceu\",\"validade\",\"vence\",\"expira\",\"termina\"]},{\"name\":\"expresso\",\"synonymous\":[]},{\"name\":\"email\",\"synonymous\":[\"e-mail\",\"imail\",\"correio eletronico\"]},{\"name\":\"exterior\",\"synonymous\":[]},{\"name\":\"empreenda\",\"synonymous\":[]},{\"name\":\"endereco\",\"synonymous\":[]},{\"name\":\"extrato\",\"synonymous\":[]}]},\"method\":\"get\",\"status\":\"success\",\"id\":\"60013938-2f01-45fa-ab9b-e0aef5a11baf\",\"from\":\"postmaster@ai.msging.net/#irismsging3\",\"pp\":\"postmaster@ai.msging.net/#irismsging3-1\",\"to\":\"botkcartoes@msging.net\"}";

        // Act
        Envelope envelope = target.deserialize(json);

        // Assert
        assertThat(envelope).isInstanceOf(Command.class);
        Command command = (Command)envelope;
        assertThat(command.getResource()).isNotNull().isInstanceOf(Entity.class);
    }

    //endregion Command

    //endregion deserialize method
}