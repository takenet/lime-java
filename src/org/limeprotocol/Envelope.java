package org.limeprotocol;

import java.util.Map;
import java.util.UUID;

/**
 * Created by and_000 on 13/01/2015.
 */
public abstract class Envelope {
    private UUID id;
    private Node from;
    private Node to;
    private Node pp;
    private Map<String, String> metadata;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }

    public Node getPp() {
        return pp;
    }

    public void setPp(Node pp) {
        this.pp = pp;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
