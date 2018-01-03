package net.take.iris.messaging.resources.artificialIntelligence;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

import java.util.Map;

/**
 * Represents the result to an analysis request.
 */
public class AnalysisResponse extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.iris.ai.analysis-response+json";

    private String id;
    private String text;
    private IntentionResponse[] intentions;
    private EntityResponse[] entities;
    private String provider;
    private String modelId;
    private Map<String, Object> providerContext;

    public AnalysisResponse() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets the analysis id.
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the analysis id.
     * @return
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the text used to process
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text used to process
     * @return
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the intentions found in the text
     * @return
     */
    public IntentionResponse[] getIntentions() {
        return intentions;
    }

    /**
     * Sets the intentions found in the text
     * @return
     */
    public void setIntentions(IntentionResponse[] intentions) {
        this.intentions = intentions;
    }

    /**
     * Gets the entities found in the text
     * @return
     */
    public EntityResponse[] getEntities() {
        return entities;
    }

    /**
     * Sets the entities found in the text
     * @return
     */
    public void setEntities(EntityResponse[] entities) {
        this.entities = entities;
    }

    /**
     * Gets the name of the provider used in the analysis.
     * @return
     */
    public String getProvider() {
        return provider;
    }

    /**
     * Sets the name of the provider used in the analysis.
     * @return
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * Gets the id of the model used in the analysis.
     * @return
     */
    public String getModelId() {
        return modelId;
    }

    /**
     * Sets the id of the model used in the analysis.
     * @return
     */
    public void setModelId(String modelId) {
        this.modelId = modelId;
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
}
