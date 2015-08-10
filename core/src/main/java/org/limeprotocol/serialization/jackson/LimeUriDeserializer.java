package org.limeprotocol.serialization.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.limeprotocol.LimeUri;
import org.limeprotocol.MediaType;

import java.io.IOException;

public class LimeUriDeserializer extends JsonDeserializer<LimeUri> {

    @Override
    public LimeUri deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String mediaTypeString = jsonParser.getText();

        return LimeUri.parse(mediaTypeString);
    }
}
