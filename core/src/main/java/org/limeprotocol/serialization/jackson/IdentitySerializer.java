package org.limeprotocol.serialization.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.limeprotocol.Identity;
import org.limeprotocol.Node;

import java.io.IOException;

public class IdentitySerializer extends JsonSerializer<Identity> {
    @Override
    public Class<Identity> handledType() {
        return Identity.class;
    }

    @Override
    public void serialize(Identity identity, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
       jsonGenerator.writeString(identity.toString());
    }
}
