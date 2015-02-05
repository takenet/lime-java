package org.limeprotocol.serialization;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import org.limeprotocol.Command;
import org.limeprotocol.Node;

public class CustomSerializerModule extends SimpleModule {

    public CustomSerializerModule() {
        super("CustomSerializers", new Version(1, 0, 0, null));
        addSerializer(Enum.class, new EnumSerializer());
        addSerializer(new NodeSerializer());
        addSerializer(new MediaTypeSerializer());
        addSerializer(new LimeUriSerializer());
        addDeserializer(Node.class, new NodeDeserializer());
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        Deserializers.Base deser = new Deserializers.Base() {
            @SuppressWarnings("unchecked")
            @Override
            public JsonDeserializer<?> findEnumDeserializer(Class<?> type,
                                                            DeserializationConfig config, BeanDescription beanDesc)
                    throws JsonMappingException {
                return new EnumDeserializer((Class<Enum<?>>) type);
            }
        };
        context.addDeserializers(deser);
        context.addBeanSerializerModifier(new BeanSerializerModifier() {
            @Override
            public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
                if (beanDesc.getBeanClass() == Command.class) {
                    return new CommandSerializer((JsonSerializer<Object>)serializer);
                }
                return super.modifySerializer(config, beanDesc, serializer);
            }
        });
    };

}
