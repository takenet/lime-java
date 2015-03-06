package org.limeprotocol.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.limeprotocol.Document;
import org.limeprotocol.JsonDocument;
import org.limeprotocol.MediaType;
import org.limeprotocol.PlainDocument;

import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class SerializationUtil {
    private static Map<MediaType, Class<? extends Document>> documentTypesMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections("org.limeprotocol.*");
        Set<Class<? extends Document>> documentTypes = reflections.getSubTypesOf(Document.class);
        for (Class<? extends Document> documentClass : documentTypes) {
            try {
                registerDocumentClass(documentClass);
            } catch (Exception e) {
                Logger.getAnonymousLogger().warning("Error registering document " + documentClass + ": " + e);
            }
        }
    }

    public static Document deserializeDocument(ObjectMapper mapper, ObjectNode node, String documentName) {
        JsonNode typeNode = node.get("type");
        if (typeNode == null) {
            return null;
        }

        MediaType mediaType = mapper.convertValue(typeNode, MediaType.class);

        JsonNode documentNode = node.get(documentName);
        node.remove(documentName);
        node.remove("type");

        Class<?> documentClass = findDocumentClassFor(mediaType);
        if (documentClass == null) {
            if (mediaType.isJson()) {
                documentClass = JsonDocument.class;
                JsonDocument jsonDocument = (JsonDocument) mapper.convertValue(documentNode, documentClass);
                jsonDocument.setMediaType(mediaType);
                return jsonDocument;
            }

            return new PlainDocument(documentNode.asText(), MediaType.parse(typeNode.asText()));
        }
        return (Document) mapper.convertValue(documentNode, documentClass);
    }

    public static void registerDocumentClass(Class<? extends Document> documentClass) {
        if (Modifier.isAbstract(documentClass.getModifiers())) {
            throw new IllegalArgumentException("The class cannot be abstract");
        }

        try {
            Document document = documentClass.getConstructor().newInstance();
            documentTypesMap.put(document.getMediaType(), documentClass);
        } catch (Exception e) {
            throw new IllegalArgumentException("The document class does not have an empty constructor");
        }
    }

    public static Class<? extends Document> findDocumentClassFor(MediaType mediaType) {
        return documentTypesMap.get(mediaType);
    }
}