package org.limeprotocol.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.limeprotocol.Command;

import java.io.IOException;

public class CommandSerializer extends StdSerializer<Command> {

    private final JsonSerializer<Object> defaultSerializer;

    protected CommandSerializer(JsonSerializer<Object> defaultSerializer) {
        super(Command.class);
        this.defaultSerializer = defaultSerializer;
    }

    @Override
    public void serialize(Command command, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if(command.getStatus() == Command.CommandStatus.PENDING) {
            command.setStatus(null);
        }

        defaultSerializer.serialize(command, jsonGenerator, serializerProvider);
    }
}
