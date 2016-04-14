package org.limeprotocol.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import org.limeprotocol.Document;
import org.limeprotocol.JsonDocument;
import org.limeprotocol.MediaType;
import org.limeprotocol.PlainDocument;
import org.limeprotocol.serialization.SerializationUtil;

public class DocumentTypeIdResolver extends TypeIdResolverBase {

    @Override
    public String idFromValue(Object o) {
        if (o instanceof Document) {
            return ((Document)o).getMediaType().toString();
        }

        return null;
    }

    @Override
    public String idFromValueAndType(Object o, Class<?> aClass) {
        return idFromValue(o);
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {

        MediaType mediaType = MediaType.parse(id);
        Class documentClass = SerializationUtil.findDocumentClassFor(mediaType);

        if (documentClass == null) {
            if (mediaType.isJson()) {
                documentClass = JsonDocument.class;
            } else {
                documentClass = PlainDocument.class;
            }
        }

        return context.getTypeFactory().constructSpecializedType(_baseType, documentClass);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
