package net.take.iris.messaging.resources.artificialIntelligence;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

import java.util.Map;

/**
 * Represents an AI analysis request.
 */
public class AnalysisRequest extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.iris.ai.analysis-request+json";

    private String modelId;
    private String text;
    private Map<String, Object> providerContext;
    private Boolean testingRequest;

    public AnalysisRequest() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets the model id to be used for analysis
     * @return
     */
    public String getModelId() {
        return modelId;
    }

    /**
     * Sets the model id to be used for analysis
     * @return
     */
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    /**
     * Gets the user input text to be analyzed.
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the user input text to be analyzed.
     * @return
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the conversation context from the configured provider.
     * @return
     */
    public Map<String, Object> getProviderContext() {
        return providerContext;
    }

    /**
     * Sets the conversation context from the configured provider.
     * @return
     */
    public void setProviderContext(Map<String, Object> providerContext) {
        this.providerContext = providerContext;
    }

    /**
     * Indicates if the request is a testing one.
     * @return
     */
    public Boolean getTestingRequest() {
        return testingRequest;
    }

    /**
     * Indicates if the request is a testing one.
     * @return
     */
    public void setTestingRequest(Boolean testingRequest) {
        this.testingRequest = testingRequest;
    }
}


