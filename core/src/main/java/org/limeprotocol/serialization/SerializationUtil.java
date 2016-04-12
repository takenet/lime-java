package org.limeprotocol.serialization;

import org.limeprotocol.Document;
import org.limeprotocol.MediaType;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods used in the serialization process.
 */
public class SerializationUtil {
    private static final Map<MediaType, Class<? extends Document>> documentTypesMap = new HashMap<>();

    /**
     * Registers a document type for deserialization support.
     * @param documentClass
     */
    public static void registerDocumentClass(Class<? extends Document> documentClass) {
        if (Modifier.isAbstract(documentClass.getModifiers())) {
            throw new IllegalArgumentException("The class cannot be abstract");
        }

        try {
            Document document = documentClass.getConstructor().newInstance();
            MediaType mediaType = document.getMediaType();

            synchronized (documentTypesMap) {
                if (documentTypesMap.containsKey(mediaType)) {
                    documentTypesMap.remove(mediaType);
                }
                documentTypesMap.put(mediaType, documentClass);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("The document class does not have an empty constructor");
        }
    }

    /**
     * Finds a document class for the specified media type.
     * @param mediaType
     * @return
     */
    public static Class<? extends Document> findDocumentClassFor(MediaType mediaType) {
        return documentTypesMap.get(mediaType);
    }
}