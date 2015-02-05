package org.limeprotocol.messaging.serialization;

import org.junit.Before;
import org.junit.Test;
import org.limeprotocol.Message;
import org.limeprotocol.messaging.contents.PlainText;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;
import org.limeprotocol.testHelpers.JsonConstants;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.limeprotocol.messaging.testHelpers.MessagingTestDummy.createTextContent;
import static org.limeprotocol.serialization.JacksonEnvelopeSerializerTest.assertJsonEnvelopeProperties;
import static org.limeprotocol.testHelpers.JsonConstants.Envelope.*;
import static org.limeprotocol.testHelpers.TestDummy.*;

public class JacksonEnvelopeMessagingSerializerTest {

    private JacksonEnvelopeSerializer target;

    @Before
    public void setUp() throws Exception {
        target = new JacksonEnvelopeSerializer();
    }

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
}