package org.limeprotocol.serialization;

import org.limeprotocol.Envelope;

/**
 * Base interface for envelope serializers.
 */
public interface EnvelopeSerializer {

    /**
     * Serialize an envelope to a string.
     * @param envelope
     * @return
     */
    String serialize(Envelope envelope);

    /**
     * Deserialize an envelope from a string.
     * @param envelopeString
     * @return
     */
    Envelope deserialize(String envelopeString);
}
