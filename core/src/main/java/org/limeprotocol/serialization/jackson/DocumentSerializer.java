package org.limeprotocol.serialization.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.limeprotocol.*;

import java.io.IOException;

public class DocumentSerializer extends StdSerializer<Document> {

    private final JsonSerializer<Object> defaultSerializer;

    protected DocumentSerializer(JsonSerializer<Object> defaultSerializer) {
        super(Document.class);
        this.defaultSerializer = defaultSerializer;
    }

    @Override
    public void serialize(Document document, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        MediaType mediaType = document.getMediaType();
        if (mediaType.isJson()) {
            defaultSerializer.serialize(document, jsonGenerator, serializerProvider);
        } else {
            jsonGenerator.writeString(document.toString());
        }
    }
}
