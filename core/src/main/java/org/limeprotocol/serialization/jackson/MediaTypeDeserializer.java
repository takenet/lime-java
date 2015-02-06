package org.limeprotocol.serialization.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.limeprotocol.MediaType;

import java.io.IOException;

public class MediaTypeDeserializer extends JsonDeserializer<MediaType> {

    @Override
    public MediaType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String mediaTypeString = jsonParser.getText();

        return MediaType.parse(mediaTypeString);
    }
}
