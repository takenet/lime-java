package net.take.iris.messaging.resources.artificialIntelligence;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

import java.util.Date;

public class Analysis extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.iris.ai.analysis+json";

    private String id;
    private Date requestDateTime;
    private String text;
    private String intention;
    private Double score;
    private AnalysisModelFeedback feedback;
    private String intentionSuggested;
    private IntentionResponse[] intentions;
    private EntityResponse[] entities;

    public Analysis() {
        super(MediaType.parse(MIME_TYPE));
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getRequestDateTime() {
        return requestDateTime;
    }

    public void setRequestDateTime(Date requestDateTime) {
        this.requestDateTime = requestDateTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIntention() {
        return intention;
    }

    public void setIntention(String intention) {
        this.intention = intention;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public AnalysisModelFeedback getFeedback() {
        return feedback;
    }

    public void setFeedback(AnalysisModelFeedback feedback) {
        this.feedback = feedback;
    }

    public String getIntentionSuggested() {
        return intentionSuggested;
    }

    public void setIntentionSuggested(String intentionSuggested) {
        this.intentionSuggested = intentionSuggested;
    }

    public IntentionResponse[] getIntentions() {
        return intentions;
    }

    public void setIntentions(IntentionResponse[] intentions) {
        this.intentions = intentions;
    }

    public EntityResponse[] getEntities() {
        return entities;
    }

    public void setEntities(EntityResponse[] entities) {
        this.entities = entities;
    }
}

