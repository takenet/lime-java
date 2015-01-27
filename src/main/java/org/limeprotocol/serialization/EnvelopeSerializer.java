package org.limeprotocol.serialization;

import org.limeprotocol.Envelope;

public interface EnvelopeSerializer {
    
    String serialize(Envelope envelope);
    
    Envelope Deserialize(String envelopeString);
    
}
