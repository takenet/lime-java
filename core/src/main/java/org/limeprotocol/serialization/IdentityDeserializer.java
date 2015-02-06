package org.limeprotocol.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.limeprotocol.Identity;

import java.io.IOException;

public class IdentityDeserializer extends JsonDeserializer<Identity> {

    @Override
    public Identity deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return Identity.parse(jsonParser.getText());
    }
}
