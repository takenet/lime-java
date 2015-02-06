package org.limeprotocol.serialization;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.limeprotocol.*;
import org.limeprotocol.security.*;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.limeprotocol.security.Authentication.AuthenticationScheme;

public class JacksonEnvelopeSerializer implements EnvelopeSerializer {
    private static Map<MediaType, Class<? extends Document>> documentTypesMap;

    private final ObjectMapper mapper;

    static {
        Reflections reflections = new Reflections("org.limeprotocol.*");

        Set<Class<? extends Document>> documentTypes = reflections.getSubTypesOf(Document.class);

        documentTypesMap = new HashMap<MediaType, Class<? extends Document>>(documentTypes.size());
        for(Class<? extends Document> documentType : documentTypes) {
            Document document = null;
            try {
                document = documentType.getConstructor().newInstance(new Object[]{});
            } catch (NoSuchMethodException e) {
            } catch (InvocationTargetException e) {
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }

            if (document != null) {
                documentTypesMap.put(document.getMediaType(), documentType);
            }
        }
    }

    public JacksonEnvelopeSerializer() {
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(Include.NON_NULL);

        SimpleModule customSerializersModule = new CustomSerializerModule();

        mapper.registerModule(customSerializersModule);
    }

    @Override
    public String serialize(Envelope envelope) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(envelope);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Envelope deserialize(String envelopeString) {
        try {
            ObjectNode node;
            node = (ObjectNode) mapper.readTree(envelopeString);

            if (node.has("content")) {
                return parseMessage(node);
            } else if (node.has("event")) {
                return mapper.convertValue(node, Notification.class);
            } else if (node.has("method")) {
                return parseCommand(node);
            } else if (node.has("state")) {
                return parseSession(node);
            } else {
                throw new IllegalArgumentException("Envelope deserialization not implemented for this value");
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("JSON string is not a valid envelope", e);
        }
    }

    private Authentication parseAuthentication(JsonNode schemeNode, JsonNode authenticationNode){
        AuthenticationScheme scheme = mapper.convertValue(schemeNode, AuthenticationScheme.class);

        if (scheme == null){
            return null;
        }
        switch (scheme) {
            case Guest:
                return new GuestAuthentication();
            case Plain:
                return mapper.convertValue(authenticationNode, PlainAuthentication.class);
            case Transport:
                return new TransportAuthentication();
            default:
                throw new IllegalArgumentException("JSON string is not a valid session envelope");
        }
    }

    private Session parseSession(ObjectNode node) {
        JsonNode schemeNode = node.get("scheme");
        JsonNode authenticationNode = node.get("authentication");

        node.remove("scheme");
        node.remove("authentication");

        Session session = mapper.convertValue(node, Session.class);
        Authentication authentication = parseAuthentication(schemeNode, authenticationNode);
        session.setAuthentication(authentication);

        return session;
    }

    private Command parseCommand(ObjectNode node) {

        Command command = mapper.convertValue(node, Command.class);

        Document document = deserializeDocument(node, "resource");
        command.setResource(document);
        return command;
    }

    private Message parseMessage(ObjectNode node){

        Message message = mapper.convertValue(node, Message.class);
        Document document = deserializeDocument(node, "content");

        message.setContent(document);
        return message;
    }

    private Document deserializeDocument(ObjectNode node, String documentName) {

        JsonNode documentNode = node.get(documentName);
        JsonNode typeNode = node.get("type");

        node.remove(documentName);
        node.remove("type");

        MediaType mediaType = mapper.convertValue(typeNode, MediaType.class);

        Class<?> clazz = documentTypesMap.get(mediaType);
        if (clazz != null) {
            return (Document) mapper.convertValue(documentNode, clazz);
        } else {
            throw new IllegalStateException("There is no document with the media type " + typeNode.asText());
        }
    }
}
