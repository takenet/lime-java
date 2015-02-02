package org.limeprotocol.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.limeprotocol.Node;

import java.io.IOException;

public class NodeSerializer extends JsonSerializer<Node> {
    @Override
    public Class<Node> handledType() {
        return Node.class;
    }

    @Override
    public void serialize(Node node, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
       jsonGenerator.writeString(node.toString());
    }
}
