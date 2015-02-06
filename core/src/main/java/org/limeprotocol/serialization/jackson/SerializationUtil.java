package org.limeprotocol.serialization.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.limeprotocol.Document;
import org.limeprotocol.JsonDocument;
import org.limeprotocol.MediaType;
import org.limeprotocol.PlainDocument;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SerializationUtil {
    private static Map<MediaType, Class<? extends Document>> documentTypesMap;

    static {
        Reflections reflections = new Reflections("org.limeprotocol.*");

        Set<Class<? extends Document>> documentTypes = reflections.getSubTypesOf(Document.class);

        documentTypesMap = new HashMap<MediaType, Class<? extends Document>>(documentTypes.size());
        for (Class<? extends Document> documentType : documentTypes) {
            Document document = null;
            try {
                document = documentType.getConstructor().newInstance(new Object[]{});
            } catch (Exception e) {
            }

            if (document != null) {
                documentTypesMap.put(document.getMediaType(), documentType);
            }
        }
    }

    public static Document deserializeDocument(ObjectMapper mapper, ObjectNode node, String documentName) {

        JsonNode typeNode = node.get("type");

        if (typeNode == null) {
            return null;
        }

        JsonNode documentNode = node.get(documentName);

        node.remove(documentName);
        node.remove("type");

        MediaType mediaType = mapper.convertValue(typeNode, MediaType.class);

        Class<?> clazz = findDocumentClassFor(mediaType);
        if (clazz == null) {

            if(typeNode.asText().endsWith("+json")){
                clazz = JsonDocument.class;
                JsonDocument jsonDocument = (JsonDocument) mapper.convertValue(documentNode, clazz);
                jsonDocument.setMediaType(MediaType.parse(typeNode.asText()));
                return jsonDocument;
            }

            return new PlainDocument(documentNode.asText(), MediaType.parse(typeNode.asText()));
        }
        return (Document) mapper.convertValue(documentNode, clazz);
    }

    public static Class<? extends Document> findDocumentClassFor(MediaType mediaType) {
        return documentTypesMap.get(mediaType);
    }


}
