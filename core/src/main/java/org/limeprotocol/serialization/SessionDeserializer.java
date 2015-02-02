package org.limeprotocol.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.limeprotocol.Session;

import java.io.IOException;

public class SessionDeserializer extends JsonDeserializer<Session> {

    public static final String SCHEME_KEY = "scheme";

    @Override
    public Session deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        ObjectNode node = (ObjectNode)oc.readTree(jsonParser);

        String schemeValue = node.with(SCHEME_KEY).asText();
        node.remove("scheme");

        Session session = new Session();
        return session;
    }
}
