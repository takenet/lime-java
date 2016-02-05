package org.limeprotocol.serialization;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.limeprotocol.*;
import org.limeprotocol.security.Authentication;
import org.limeprotocol.security.GuestAuthentication;
import org.limeprotocol.security.PlainAuthentication;
import org.limeprotocol.security.TransportAuthentication;
import org.limeprotocol.serialization.jackson.CustomSerializerModule;

import java.io.IOException;

import static org.limeprotocol.security.Authentication.AuthenticationScheme;
import static org.limeprotocol.serialization.SerializationUtil.*;

public class JacksonEnvelopeSerializer implements EnvelopeSerializer {

    private final ObjectMapper mapper;

    public JacksonEnvelopeSerializer() {
        this.mapper = new ObjectMapper()
                .setSerializationInclusion(Include.NON_NULL)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new CustomSerializerModule());
    }

    @Override
    public String serialize(Envelope envelope) {
        try {
            return mapper.writeValueAsString(envelope);
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

    private Authentication parseAuthentication(JsonNode schemeNode, JsonNode authenticationNode) {
        AuthenticationScheme scheme = mapper.convertValue(schemeNode, AuthenticationScheme.class);
        if (scheme == null) {
            return null;
        }
        switch (scheme) {
            case GUEST:
                return new GuestAuthentication();
            case PLAIN:
                return mapper.convertValue(authenticationNode, PlainAuthentication.class);
            case TRANSPORT:
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
        Document document = deserializeDocument(mapper, node, "resource");
        Command command = mapper.convertValue(node, Command.class);
        command.setResource(document);

        return command;
    }

    private Message parseMessage(ObjectNode node) {
        Document document = deserializeDocument(mapper, node, "content");
        Message message = mapper.convertValue(node, Message.class);
        message.setContent(document);
        
        return message;
    }

    private static Document deserializeDocument(ObjectMapper mapper, ObjectNode node, String documentName) {
        JsonNode typeNode = node.get("type");
        if (typeNode == null) return null;

        MediaType mediaType = mapper.convertValue(typeNode, MediaType.class);
        if (mediaType == null) return null;

        JsonNode documentNode = node.get(documentName);
        if (documentNode == null) {
            if (mediaType.isJson()) {
                return new JsonDocument(mediaType);
            } else {
                return new PlainDocument(mediaType);
            }
        }

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
}
