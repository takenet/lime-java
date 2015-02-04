package org.limeprotocol.serialization;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.limeprotocol.Envelope;
import org.limeprotocol.Message;
import org.limeprotocol.Session;
import org.limeprotocol.security.Authentication;
import org.limeprotocol.security.GuestAuthentication;
import org.limeprotocol.security.PlainAuthentication;
import org.limeprotocol.security.TransportAuthentication;

import java.io.IOException;

import static org.limeprotocol.security.Authentication.AuthenticationScheme;

public class JacksonEnvelopeSerializer implements EnvelopeSerializer {
    private final ObjectMapper mapper;

    public JacksonEnvelopeSerializer() {
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(Include.NON_NULL);

        SimpleModule customSerializersModule = new CustomSerializerModule();

        mapper.registerModule(customSerializersModule);
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
                return null;
            } else if (node.has("method")) {
                return null;
            } else if (node.has("state")) {
                return parseSession(node);
            } else {
                throw new IllegalArgumentException("Envelope deserialization not implemented for this value");
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("JSON string is not a valid envelope", e);
        }
    }

    private Session parseSession(ObjectNode node) {
        JsonNode schemeNode = node.get("scheme");
        JsonNode authenticationNode = node.get("authentication");
        node.remove("scheme");
        node.remove("authentication");

        AuthenticationScheme scheme = mapper.convertValue(schemeNode, AuthenticationScheme.class);

        Class<?> authenticationClass;
        switch (scheme) {
            case Guest:
                authenticationClass = GuestAuthentication.class;
                break;
            case Plain:
                authenticationClass = PlainAuthentication.class;
                break;
            case Transport:
                authenticationClass = TransportAuthentication.class;
                break;
            default:
                throw new IllegalArgumentException("JSON string is not a valid session envelope");
        }

        Session session = mapper.convertValue(node, Session.class);
        Authentication plainAuthentication = (Authentication) mapper.convertValue(authenticationNode, authenticationClass);
        session.setAuthentication(plainAuthentication);

        return session;
    }

    private Message parseMessage(ObjectNode node){
        return null;
    }
}