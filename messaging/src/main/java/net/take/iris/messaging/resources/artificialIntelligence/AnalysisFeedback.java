package net.take.iris.messaging.resources.artificialIntelligence;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

public class AnalysisFeedback extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.iris.ai.analysis-feedback+json";

    private AnalysisModelFeedback feedback;
    private String intentionId;

    public AnalysisFeedback() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets the analysis feedback.
     * @return
     */
    public AnalysisModelFeedback getFeedback() {
        return feedback;
    }

    /**
     * Sets the analysis feedback.
     * @param feedback
     */
    public void setFeedback(AnalysisModelFeedback feedback) {
        this.feedback = feedback;
    }

    /**
     * Gets the intention id to associate to the analyzed input.
     * @return
     */
    public String getIntentionId() {
        return intentionId;
    }

    /**
     * Sets the intention id to associate to the analyzed input.
     * @return
     */
    public void setIntentionId(String intentionId) {
        this.intentionId = intentionId;
    }
}
