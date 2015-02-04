package org.limeprotocol.network;

/**
 * Enable request tracing for network transports.
 */
public interface TraceWriter {
    /**
     * Trace some data.
     * @param data
     * @param operation
     */
    void trace(String data, DataOperation operation);

    /**
     * Indicates if the tracer is enabled.
     * @return
     */
    boolean isEnabled();
    
    /**
     * Indicates if the tracer is enabled.
     * */
    public enum DataOperation {
        /**
         * A transport send operation.
         */
        SEND,
        /**
         * A transport receive operation.
         */
        RECEIVE
    }
}
