package org.limeprotocol.serialization;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

import java.io.IOException;

/**
 * Lowercase serializer used for {@link java.lang.Enum} types.
 *<p>
 * Based on {@link StdScalarSerializer} since the JSON value is
 * scalar (String).
 *
 * See http://jira.codehaus.org/browse/JACKSON-861
 *
 * @author ceefour
 * @see <a href="https://jira.codehaus.org/browse/JACKSON-861">workaround</a>
 */
@SuppressWarnings("rawtypes")
public class EnumSerializer extends StdScalarSerializer<Enum> {

    public EnumSerializer() {
        super(Enum.class, false);
    }

    @Override
    public void serialize(Enum value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeString(value.name().toLowerCase());
    }
}