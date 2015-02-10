package org.limeprotocol.serialization.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import org.limeprotocol.util.StringUtils;

import java.io.IOException;

/**
 * Deserializer class that can deserialize instances of
 * specified Enum class from Strings and Integers,
 * uppercasing before deserialization.
 * @author ceefour
 * @see <a href="https://jira.codehaus.org/browse/JACKSON-861">workaround</a>
 */
public class EnumDeserializer extends StdScalarDeserializer<Enum<?>> {

    protected EnumDeserializer(Class<Enum<?>> clazz) {
        super(clazz);
    }

    @Override
    public Enum<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String text = jp.getText();
        Class<?> type = handledType();
        for (Object value: type.getEnumConstants()) {
            Enum<?> enumValue = (Enum<?>)type.cast(value);
            if (StringUtils.toCamelCase(enumValue.toString()).equals(text)) {
                return enumValue;
            }
        }

        throw new RuntimeException("Cannot deserialize enum " + type.getName() + " from " + text);
    }

}