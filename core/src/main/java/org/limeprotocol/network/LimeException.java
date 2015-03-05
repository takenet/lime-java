package org.limeprotocol.network;

import org.limeprotocol.Reason;

public class LimeException extends RuntimeException {
    
    private final Reason reason;
    
    public LimeException(int reasonCode, String reasonDescription) {
        this(new Reason(reasonCode, reasonDescription));
    }
    
    public LimeException(Reason reason) {
        
        if (reason == null) {
            throw new IllegalArgumentException("reason");
        }
            
        this.reason = reason;
    }

    /**
     * Gets the reason associated to the exception.
     * @return
     */
    public Reason getReason() {
        return reason;
    }
}