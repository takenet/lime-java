package org.limeprotocol.serialization;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.*;
import org.codehaus.jackson.map.module.SimpleModule;
import org.limeprotocol.Envelope;
import org.limeprotocol.Message;
import org.limeprotocol.Session;

import java.io.IOException;

public class EnvelopeSerializerImpl implements EnvelopeSerializer {
    private final ObjectMapper mapper;

    public EnvelopeSerializerImpl() {
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(Inclusion.NON_NULL);

        SimpleModule customSerializersModule = new SimpleModule("CustomSerializers",
                new Version(1,0,0,null));
        customSerializersModule.addSerializer(new NodeSerializer());
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
        Class clazz;
        if (envelopeString.contains("\"state\"")) {
            clazz = Session.class;
        } else {
            throw new IllegalArgumentException("JSON string is not a valid envelope");
        }

        try {
            return (Envelope) mapper.readValue(envelopeString, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON string is not a valid envelope", e);
        }
    }
}
