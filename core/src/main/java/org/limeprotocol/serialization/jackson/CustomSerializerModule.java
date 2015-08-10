package org.limeprotocol.serialization.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import org.limeprotocol.*;
import org.limeprotocol.security.Authentication;

import java.util.Iterator;
import java.util.List;

import static org.limeprotocol.security.Authentication.AuthenticationScheme;

public class CustomSerializerModule extends SimpleModule {

    public CustomSerializerModule() {
        super("CustomSerializers", new Version(1, 0, 0, null));
        addSerializer(Enum.class, new EnumSerializer());
        addSerializer(new NodeSerializer());
        addSerializer(new IdentitySerializer());
        addSerializer(new MediaTypeSerializer());
        addSerializer(new LimeUriSerializer());

        addDeserializer(Node.class, new NodeDeserializer());
        addDeserializer(Identity.class, new IdentityDeserializer());
        addDeserializer(MediaType.class, new MediaTypeDeserializer());
        addDeserializer(LimeUri.class, new LimeUriDeserializer());
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

                if (Document.class.isAssignableFrom(beanClass)) {
                    return new DocumentSerializer((JsonSerializer<Object>) serializer);
                }
                return super.modifySerializer(config, beanDesc, serializer);
            }

            @Override
            public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
                Class<?> beanClass = beanDesc.getBeanClass();

                if(Document.class.isAssignableFrom(beanClass)){
                    removeProperty(MediaType.class, beanProperties);
                } else if (Authentication.class.isAssignableFrom(beanClass)) {
                    removeProperty(AuthenticationScheme.class, beanProperties);
                }
                return super.changeProperties(config, beanDesc, beanProperties);
            }

            private void removeProperty(Class type, List<BeanPropertyWriter> beanProperties) {
                for (Iterator<BeanPropertyWriter> iterator = beanProperties.iterator(); iterator.hasNext(); ) {
                    BeanPropertyWriter propertyWriter = iterator.next();
                    if(propertyWriter.getPropertyType() == type) {
                        iterator.remove();
                        break;
                    }
                }
            }
        });
    };

}
