package org.limeprotocol.messaging.contents;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.DocumentContainer;
import org.limeprotocol.MediaType;
import org.limeprotocol.Node;

/**
 * Indicates to a node to redirect to another address in order to continue the current conversation.
 * It is useful to handover the current conversation to another connected nodes.
 */
public class Redirect extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.lime.redirect+json";

    private Node address;
    private DocumentContainer context;

    public Redirect() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets the redirect address.
     * @return
     */
    public Node getAddress() {
        return address;
    }

    /**
     * Sets the redirect address.
     * @return
     */
    public void setAddress(Node address) {
        this.address = address;
    }

    /**
     * Gets the state data to be forwarded to the redirect address in order to keep the conversation context.
     * @return
     */
    public DocumentContainer getContext() {
        return context;
    }

    /**
     * Sets the state data to be forwarded to the redirect address in order to keep the conversation context.
     * @return
     */
    public void setContext(DocumentContainer context) {
        this.context = context;
    }
}
