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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.limeprotocol.security.Authentication.AuthenticationScheme;

public class CustomSerializerModule extends SimpleModule {

    private final Class<?> ignoreDocumentContainerClass;

    public CustomSerializerModule() {
        this(null);
    }

    CustomSerializerModule(Class<?> ignoreDocumentContainerClass) {
        super("CustomSerializers", new Version(1, 0, 0, null));
        this.ignoreDocumentContainerClass = ignoreDocumentContainerClass;

        // Custom serializers
        addSerializer(Enum.class, new EnumSerializer());
        addSerializer(new NodeSerializer());
        addSerializer(new IdentitySerializer());
        addSerializer(new MediaTypeSerializer());
        addSerializer(new LimeUriSerializer());

        // Custom deserializers
        addDeserializer(Node.class, new NodeDeserializer());
        addDeserializer(Identity.class, new IdentityDeserializer());
        addDeserializer(MediaType.class, new MediaTypeDeserializer());
        addDeserializer(LimeUri.class, new LimeUriDeserializer());
        addDeserializer(DocumentCollection.class, new DocumentCollectionDeserializer());
        addDeserializer(Document.class, new DocumentDeserializer());
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);

        Deserializers.Base deserializers = new Deserializers.Base() {
            @SuppressWarnings("unchecked")
            @Override
            public JsonDeserializer<?> findEnumDeserializer(Class<?> type,
                                                            DeserializationConfig config, BeanDescription beanDesc)
                    throws JsonMappingException {
                return new EnumDeserializer((Class<Enum<?>>) type);
            }
        };
        context.addDeserializers(deserializers);
        context.addBeanDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                try {
                    // Check for custom document container deserializers.
                    // DocumentContainers are classes that have a 'MediaType' and a 'Document' properties, like Message, Command, and other contained objects.
                    Class documentContainerClass = beanDesc.getBeanClass();
                    if (!documentContainerClass.equals(ignoreDocumentContainerClass) &&
                            !Modifier.isAbstract(documentContainerClass.getModifiers())) {
                        Method method = documentContainerClass.getMethod("getType");
                        if (method.getReturnType().equals(MediaType.class)) {
                            for (Method setDocumentMethod : documentContainerClass.getMethods()) {
                                String methodName = setDocumentMethod.getName();
                                if (methodName.startsWith("set") &&
                                        setDocumentMethod.getParameterCount() == 1 &&
                                        setDocumentMethod.getParameterTypes()[0] == Document.class) {

                                    String documentNodeName = methodName.substring(3, methodName.length()).toLowerCase();
                                    return new DocumentContainerDeserializer<>(documentContainerClass, documentNodeName);
                                }
                            }
                        }
                    }
                } catch (NoSuchMethodException e) {

                } catch (Exception e) {
                    e.printStackTrace();
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

                if (Document.class.isAssignableFrom(beanClass)) {
                    removeProperty(MediaType.class, beanProperties);
                } else if (Authentication.class.isAssignableFrom(beanClass)) {
                    removeProperty(AuthenticationScheme.class, beanProperties);
                }
                return super.changeProperties(config, beanDesc, beanProperties);
            }

            private void removeProperty(Class type, List<BeanPropertyWriter> beanProperties) {
                for (Iterator<BeanPropertyWriter> iterator = beanProperties.iterator(); iterator.hasNext(); ) {
                    BeanPropertyWriter propertyWriter = iterator.next();
                    if (propertyWriter.getPropertyType() == type) {
                        iterator.remove();
                        break;
                    }
                }
            }
        });
    }

    private <T> SimpleModule addDocumentContainerDeserializer(Class<T> type, String documentNodeName) {
        // This is needed to avoid StackOverflow inside the DocumentContainerDeserializer class, since it uses the mapper to deserialize the object.
        if (!type.equals(ignoreDocumentContainerClass)) {
            DocumentContainerDeserializer deserializer = new DocumentContainerDeserializer<>(type, documentNodeName);
            addDeserializer(type, deserializer);
        }
        return this;
    }
}
