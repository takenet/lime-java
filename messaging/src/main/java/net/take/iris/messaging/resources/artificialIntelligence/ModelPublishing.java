package net.take.iris.messaging.resources.artificialIntelligence;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

public class ModelPublishing extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.iris.ai.model-publishing+json";

    private String id;

    public ModelPublishing() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets the model id.
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the model id.
     * @return
     */
    public void setId(String id) {
        this.id = id;
    }

}