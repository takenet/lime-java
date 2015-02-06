package org.limeprotocol.serialization.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.limeprotocol.MediaType;

import java.io.IOException;

public class MediaTypeSerializer extends JsonSerializer<MediaType> {

    @Override
    public Class<MediaType> handledType() {
        return MediaType.class;
    }

    @Override
    public void serialize(MediaType mediaType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeString(mediaType.toString());
    }
}
