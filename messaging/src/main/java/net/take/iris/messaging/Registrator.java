package net.take.iris.messaging;

import net.take.iris.messaging.resources.*;
import net.take.iris.messaging.resources.Thread;
import net.take.iris.messaging.resources.artificialIntelligence.*;
import org.limeprotocol.serialization.SerializationUtil;

public class Registrator {

    /**
     * Register the documents in the package.
     */
    public static void registerDocuments() {
        SerializationUtil.registerDocumentClass(DistributionList.class);
        SerializationUtil.registerDocumentClass(EventTrack.class);
        SerializationUtil.registerDocumentClass(Schedule.class);
        SerializationUtil.registerDocumentClass(Thread.class);
        SerializationUtil.registerDocumentClass(ThreadMessage.class);
        SerializationUtil.registerDocumentClass(Tunnel.class);

        // AI
        SerializationUtil.registerDocumentClass(Analysis.class);
        SerializationUtil.registerDocumentClass(AnalysisFeedback.class);
        SerializationUtil.registerDocumentClass(AnalysisRequest.class);
        SerializationUtil.registerDocumentClass(AnalysisResponse.class);
        SerializationUtil.registerDocumentClass(Answer.class);
        SerializationUtil.registerDocumentClass(Entity.class);
        SerializationUtil.registerDocumentClass(Intention.class);
        SerializationUtil.registerDocumentClass(Model.class);
        SerializationUtil.registerDocumentClass(ModelPublishing.class);
        SerializationUtil.registerDocumentClass(ModelTraining.class);
        SerializationUtil.registerDocumentClass(Question.class);
    }
}
