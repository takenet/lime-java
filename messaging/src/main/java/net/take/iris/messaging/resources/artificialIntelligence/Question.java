package net.take.iris.messaging.resources.artificialIntelligence;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

public class Question extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.iris.ai.question+json";

    private String id;
    private String text;

    public Question() {
        super(MediaType.parse(MIME_TYPE));
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}