package org.limeprotocol;

import java.util.UUID;

/**
 * Utility class for generating envelope ids.
 */
public class EnvelopeId {
    /**
     * Generates a new envelope identifier.
     * @return
     */
    public static String newId() {
        return UUID.randomUUID().toString();
    }
}
