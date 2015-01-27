package org.limeprotocol.serialization;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
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
