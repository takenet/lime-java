package org.limeprotocol.serialization.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.limeprotocol.LimeUri;

import java.io.IOException;

public class LimeUriSerializer extends JsonSerializer<LimeUri> {
    @Override
    public Class<LimeUri> handledType() {
        return LimeUri.class;
    }

    @Override
    public void serialize(LimeUri limeUri, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeString(limeUri.toString());
    }
}
