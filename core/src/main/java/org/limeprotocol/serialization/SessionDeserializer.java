package org.limeprotocol.serialization;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.node.ObjectNode;
import org.limeprotocol.Session;

import java.io.IOException;

public class SessionDeserializer extends JsonDeserializer<Session> {

    public static final String SCHEME_KEY = "scheme";

    @Override
    public Session deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        ObjectNode node = (ObjectNode)oc.readTree(jsonParser);

        String schemeValue = node.with(SCHEME_KEY).getValueAsText();
        node.remove("scheme");

        Session session = new Session();
        return session;
    }
}
