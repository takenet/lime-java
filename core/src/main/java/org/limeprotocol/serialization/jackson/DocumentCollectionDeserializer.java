package org.limeprotocol.serialization.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.limeprotocol.Document;
import org.limeprotocol.DocumentCollection;
import org.limeprotocol.MediaType;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;
import org.limeprotocol.serialization.SerializationUtil;

import java.io.IOException;
import java.util.Iterator;

public class DocumentCollectionDeserializer extends JsonDeserializer<DocumentCollection> {

    public DocumentCollectionDeserializer() {

    }

    @Override
    public DocumentCollection deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec objectCodec = jsonParser.getCodec();
        ObjectNode objectNode = objectCodec.readTree(jsonParser);
        DocumentCollection collection = new DocumentCollection();

        MediaType itemType = MediaType.parse(objectNode.get("itemType").asText());
        ArrayNode documentsNode = (ArrayNode) objectNode.get("items");
        Document[] items = new Document[documentsNode.size()];

        int i = 0;
        for (Iterator iterator = documentsNode.elements(); iterator.hasNext(); i++) {
            ObjectNode documentNode = (ObjectNode) iterator.next();
            Document document = DocumentContainerDeserializer.getDocument(documentNode, itemType, JacksonEnvelopeSerializer.getObjectMapper());
            items[i] = document;
        }

        int total = 0;
        if (objectNode.has("total")) {
            total = objectNode.get("total").asInt();
        }

        collection.setTotal(total);
        collection.setItemType(itemType);
        collection.setItems(items);

        return collection;
    }
}