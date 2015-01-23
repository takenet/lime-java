package org.limeprotocol;

import java.util.UUID;

public class Message extends Envelope {

    public final String TYPE_KEY = "type";
    public final String CONTENT_KEY = "content";

    private MediaType Type;
    private Document content;

    public Message()
    {
    }

    public Message(UUID id)
    {
        super(id);
    }

    public MediaType getType() {
        if(content != null){
            return content.getMediaType();
        }
        return null;
    }

    public Document getContent() {
        return content;
    }

    public void setContent(Document content) {
        this.content = content;
    }

}
