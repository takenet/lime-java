package org.limeprotocol.serialization;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import org.limeprotocol.*;
import org.limeprotocol.security.*;
import org.limeprotocol.serialization.jackson.CustomSerializerModule;

import java.io.IOException;

import static org.limeprotocol.security.Authentication.AuthenticationScheme;

public class JacksonEnvelopeSerializer implements EnvelopeSerializer {

    private final static ObjectMapper templateObjectMapper;
    private final static ObjectMapper objectMapper;

    static {
        templateObjectMapper = new ObjectMapper()
                .setSerializationInclusion(Include.NON_NULL)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setDateFormat(new ISO8601DateFormat());

        objectMapper = createObjectMapper().registerModule(new CustomSerializerModule());
    }

    public static ObjectMapper createObjectMapper() {
        return templateObjectMapper.copy();
    }

    public static ObjectMapper getObjectMapper() { return objectMapper; }

    @Override
    public String serialize(Envelope envelope) {
        try {
            return getObjectMapper().writeValueAsString(envelope);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Envelope deserialize(String envelopeString) {
        try {
            ObjectNode node;
            node = (ObjectNode) getObjectMapper().readTree(envelopeString);

            if (node.has("content")) {
                return  getObjectMapper().convertValue(node, Message.class);
            } else if (node.has("event")) {
                return getObjectMapper().convertValue(node, Notification.class);
            } else if (node.has("method")) {
                return  getObjectMapper().convertValue(node, Command.class);
            } else if (node.has("state")) {
                return deserializeSession(node);
            } else {
                throw new IllegalArgumentException("Envelope deserialization not implemented for this value");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON string is not a valid envelope", e);
        }
    }

    private Session deserializeSession(ObjectNode node) {
        JsonNode schemeNode = node.get("scheme");
        JsonNode authenticationNode = node.get("authentication");

        node.remove("scheme");
        node.remove("authentication");

        Session session = getObjectMapper().convertValue(node, Session.class);
        Authentication authentication = deserializeAuthentication(schemeNode, authenticationNode);
        session.setAuthentication(authentication);

        return session;
    }

    private Authentication deserializeAuthentication(JsonNode schemeNode, JsonNode authenticationNode) {
        AuthenticationScheme scheme = getObjectMapper().convertValue(schemeNode, AuthenticationScheme.class);
        if (scheme == null) {
            return null;
        }
        switch (scheme) {
            case GUEST:
                return new GuestAuthentication();
            case PLAIN:
                return getObjectMapper().convertValue(authenticationNode, PlainAuthentication.class);
            case TRANSPORT:
                return new TransportAuthentication();
            case KEY:
                return new KeyAuthentication();
            case EXTERNAL:
                return new ExternalAuthentication();
            default:
                throw new IllegalArgumentException("JSON string is not a valid session envelope");
        }
    }
}
