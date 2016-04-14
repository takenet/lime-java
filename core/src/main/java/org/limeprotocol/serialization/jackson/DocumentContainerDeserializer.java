package org.limeprotocol.serialization.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.limeprotocol.Document;
import org.limeprotocol.JsonDocument;
import org.limeprotocol.MediaType;
import org.limeprotocol.PlainDocument;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;
import org.limeprotocol.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.limeprotocol.serialization.SerializationUtil.findDocumentClassFor;

public class DocumentContainerDeserializer<T> extends JsonDeserializer<T> {

    private static final Map<Class, ObjectMapper> documentContainerObjectMapperMap = new HashMap<>();
    private static final Object syncRoot = new Object();

    private final Class<T> type;
    private final String documentNodeName;

    public DocumentContainerDeserializer(Class<T> type, String documentNodeName) {
        this.type = type;
        this.documentNodeName = documentNodeName;
    }

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec objectCodec = jsonParser.getCodec();
        ObjectNode objectNode = objectCodec.readTree(jsonParser);

        ObjectMapper mapper = getObjectMapper();
        Document document = deserializeDocument(objectNode, documentNodeName, mapper);
        T value = mapper.convertValue(objectNode, type);

        if (document != null) {
            try {
                Method method = type.getMethod("set" + StringUtils.toProperCase(documentNodeName), Document.class);
                method.invoke(value, document);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return value;
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper mapper = documentContainerObjectMapperMap.get(type);
        if (mapper == null) {
            synchronized (syncRoot) {
                mapper = documentContainerObjectMapperMap.get(type);
                if (mapper == null) {
                    // Creates a new mapper that excludes the current type in the custom serialization registration
                    // to avoid stackoverflow exceptions in the convertValue call bellow.
                    mapper = JacksonEnvelopeSerializer.createObjectMapper().registerModule(new CustomSerializerModule(type));
                    documentContainerObjectMapperMap.put(type, mapper);
                }
            }
        }
        return mapper;
    }

    private static Document deserializeDocument(ObjectNode node, String documentNodeName, ObjectMapper mapper) {
        JsonNode typeNode = node.get("type");
        if (typeNode == null) return null;
        MediaType mediaType = mapper.convertValue(typeNode, MediaType.class);
        if (mediaType == null) return null;

        JsonNode documentNode = node.get(documentNodeName);
        if (documentNode == null) {
            if (mediaType.isJson()) {
                return new JsonDocument(mediaType);
            } else {
                return new PlainDocument(mediaType);
            }
        }

        node.remove(documentNodeName);
        node.remove("type");

        return getDocument(documentNode, mediaType, mapper);
    }

    static Document getDocument(JsonNode documentNode, MediaType mediaType, ObjectMapper mapper) {
        Class<?> documentClass = findDocumentClassFor(mediaType);
        if (documentClass == null) {
            if (mediaType.isJson()) {
                documentClass = JsonDocument.class;
                JsonDocument jsonDocument = (JsonDocument) mapper.convertValue(documentNode, documentClass);
                jsonDocument.setMediaType(mediaType);
                return jsonDocument;
            }

            return new PlainDocument(documentNode.asText(), mediaType);
        }
        return (Document) mapper.convertValue(documentNode, documentClass);
    }
}
