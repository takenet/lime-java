package org.limeprotocol.serialization.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.limeprotocol.Document;

import java.io.IOException;

public class DocumentDeserializer extends JsonDeserializer<Document> {


    @Override
    public Document deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        JsonNode node = jsonParser.readValueAsTree();

        ObjectCodec objectCodec = jsonParser.getCodec();
        ObjectNode objectNode = objectCodec.readTree(jsonParser);

        JsonStreamContext parentStreamContext = jsonParser.getParsingContext().getParent();

        if (parentStreamContext != null) {
            Object parentValue = parentStreamContext.getCurrentValue();


        }
        return null;
    }
}
