package org.limeprotocol.serialization;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.limeprotocol.Envelope;
import org.limeprotocol.Node;
import org.limeprotocol.Session;

import java.io.IOException;

public class EnvelopeSerializerImpl implements EnvelopeSerializer {
    private final ObjectMapper mapper;

    public EnvelopeSerializerImpl() {
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(Include.NON_NULL);

        SimpleModule customSerializersModule = new SimpleModule("CustomSerializers",
                new Version(1,0,0,null))
            .addSerializer(new NodeSerializer())
            .addDeserializer(Node.class, new NodeDeserializer());

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

            if (node.has("state")) {
                JsonNode schemeNode = node.get("scheme");
                JsonNode authenticationNode = node.get("authentication");
                node.remove("scheme");
                node.remove("authentication");
                return mapper.convertValue(node, Session.class);
            }
            else {
                throw new IllegalArgumentException("Envelope deserialization not implemented for this value");
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("JSON string is not a valid envelope", e);
        }
    }
}
