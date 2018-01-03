package net.take.iris.messaging.resources.artificialIntelligence;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

public class ModelTraining extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.iris.ai.model-training+json";

    public ModelTraining() {
        super(MediaType.parse(MIME_TYPE));
    }
}