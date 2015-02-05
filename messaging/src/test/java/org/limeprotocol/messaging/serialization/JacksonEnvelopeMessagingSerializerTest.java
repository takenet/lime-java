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

}