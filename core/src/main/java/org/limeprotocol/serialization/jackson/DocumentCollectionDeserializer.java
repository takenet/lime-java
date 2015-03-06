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
import org.limeprotocol.serialization.SerializationUtil;

import java.io.IOException;
import java.util.Iterator;

public class DocumentCollectionDeserializer extends JsonDeserializer<DocumentCollection> {
    private final JsonDeserializer<Object> deserializer;

    public DocumentCollectionDeserializer(JsonDeserializer<Object> deserializer) {

        this.deserializer = deserializer;
    }

    @Override
    public DocumentCollection deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        ObjectNode node = oc.readTree(jsonParser);

        DocumentCollection collection = new DocumentCollection();

        int total = node.get("total").asInt();
        Document[] items = new Document[total];
        MediaType itemType = MediaType.parse(node.get("itemType").asText());

        ArrayNode documentsNode = (ArrayNode) node.get("items");

        Class<?> documentClass = SerializationUtil.findDocumentClassFor(itemType);
        if (documentClass != null) {
            int i = 0;
            for (Iterator iterator = documentsNode.elements(); iterator.hasNext(); i++) {
                ObjectNode documentNode = (ObjectNode) iterator.next();
                Document document = (Document)oc.readValue(documentNode.traverse(), documentClass);
                items[i] = document;
            }
        }

        collection.setTotal(total);
        collection.setItemType(itemType);
        collection.setItems(items);

        return collection;
    }
}
