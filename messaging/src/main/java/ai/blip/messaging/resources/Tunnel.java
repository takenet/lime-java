package ai.blip.messaging.resources;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.Identity;
import org.limeprotocol.MediaType;
import org.limeprotocol.Node;

public class Tunnel extends DocumentBase {
    public static String MIME_TYPE = "application/vnd.iris.tunnel+json";

    /**
     * Initializes a new instance of the @Tunnel class.
     */
    public Tunnel() {
        super(MediaType.parse(MIME_TYPE));
    }

    private Identity owner;
    private Node originator;
    private Identity destination;

    /**
     * Get the tunnel owner identity, which receives the envelopes from the destination.
     *
     * @return
     */
    public Identity getOwner() {
        return owner;
    }

    /**
     * Set the tunnel owner identity, which receives the envelopes from the destination.
     *
     * @param owner
     */
    public void setOwner(Identity owner) {
        this.owner = owner;
    }

    /**
     * Get the original sender of the tunnel envelope.
     *
     * @return
     */
    public Node getOriginator() {
        return originator;
    }

    /**
     * Set the original sender of the tunnel envelope.
     *
     * @param originator
     */
    public void setOriginator(Node originator) {
        this.originator = originator;
    }

    /**
     * Get the destination which will receive envelopes from the tunnel.
     *
     * @return
     */
    public Identity getDestination() {
        return destination;
    }

    /**
     * Set the destination which will receive envelopes from the tunnel.
     *
     * @param destination
     */
    public void setDestination(Identity destination) {
        this.destination = destination;
    }
}