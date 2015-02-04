package org.limeprotocol.messaging;

import org.limeprotocol.Document;
import org.limeprotocol.MediaType;

/**
 * Allows the nodes to manage the
 * session envelope quota configuration.
 */
public class Quota implements Document {
    public final String MIME_TYPE = "application/vnd.lime.quota+json";
    private MediaType mediaType;

    public Quota() {
        this.mediaType = MediaType.parse(MIME_TYPE);
    }

    /**
     * The number of envelopes that the node
     * can originate in the current session.
     * If the value is 0 or is
     * not defined, this limit is unbounded.
     * If a session exceeds the limit, it can be finished by the server.
     */
    public Integer threshold;

    /**
     * Indicates the cap of envelopes per second that the
     * session can send and receive. If the value is 0 or is
     * not defined, this capability is unbounded.
     * If a session exceeds the limit, it can be finished by the server.
     */
    public Integer throughput;

    /**
     * The uncompressed size limit of the envelopes that
     * can be sent in the session. If the value is 0 or is
     * not defined, this capability is unbounded.
     * If a envelope sent in the session exceeds the limit, it can be finished
     * by the server.
     */
    public Integer maxEnvelopeSize;

    public Integer getMaxEnvelopeSize() {
        return maxEnvelopeSize;
    }

    public void setMaxEnvelopeSize(Integer maxEnvelopeSize) {
        this.maxEnvelopeSize = maxEnvelopeSize;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public Integer getThroughput() {
        return throughput;
    }

    public void setThroughput(Integer throughput) {
        this.throughput = throughput;
    }

    @Override
    public MediaType getMediaType() {
        return this.mediaType;
    }
}
