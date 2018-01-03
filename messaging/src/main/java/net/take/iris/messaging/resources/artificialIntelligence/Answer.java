package net.take.iris.messaging.resources.artificialIntelligence;

import org.limeprotocol.Document;
import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

public class Answer extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.iris.ai.answer+json";

    private String id;
    private Document value;

    public Answer() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets the answer unique id.
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the answer unique id.
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the value media type.
     * @return
     */
    public MediaType getType() {
        if (value != null) return value.getMediaType();
        return null;
    }

    /**
     * Gets the answer document value.
     * @return
     */
    public Document getValue() {
        return value;
    }

    /**
     * Sets the answer document value.
     * @return
     */
    public void setValue(Document value) {
        this.value = value;
    }
}
