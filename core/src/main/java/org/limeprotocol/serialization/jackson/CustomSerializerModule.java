package org.limeprotocol.serialization.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import org.limeprotocol.*;

public class CustomSerializerModule extends SimpleModule {

    public CustomSerializerModule() {
        super("CustomSerializers", new Version(1, 0, 0, null));
        addSerializer(Enum.class, new EnumSerializer());
        addSerializer(new NodeSerializer());
        addSerializer(new MediaTypeSerializer());
        addSerializer(new LimeUriSerializer());
        addDeserializer(Node.class, new NodeDeserializer());
        addDeserializer(Identity.class, new IdentityDeserializer());
        addDeserializer(MediaType.class, new MediaTypeDeserializer());
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
        context.addBeanDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                if (beanDesc.getBeanClass() == DocumentCollection.class) {
                    return new DocumentCollectionDeserializer((JsonDeserializer<Object>) deserializer);
                }
                return super.modifyDeserializer(config, beanDesc, deserializer);
            }
        });
        context.addBeanSerializerModifier(new BeanSerializerModifier() {
            @Override
            public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
                Class<?> beanClass = beanDesc.getBeanClass();
                if (beanClass == Command.class) {
                    return new CommandSerializer((JsonSerializer<Object>) serializer);
                }

                if (Document.class.isAssignableFrom(beanClass)) {
                    return new DocumentSerializer((JsonSerializer<Object>) serializer);
                }
                return super.modifySerializer(config, beanDesc, serializer);
            }
        });
    };

}
