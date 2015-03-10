package org.limeprotocol.messaging.resources;

import org.limeprotocol.Document;
import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

public class Ping extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.lime.ping+json";

    public Ping() {
        super(MediaType.parse(MIME_TYPE));
    }
}
