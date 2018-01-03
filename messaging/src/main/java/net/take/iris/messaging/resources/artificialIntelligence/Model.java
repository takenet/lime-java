package net.take.iris.messaging.resources.artificialIntelligence;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

import java.net.URI;
import java.util.Date;

/**
 * Represents a complete AI model.
 */
public class Model extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.iris.ai.model+json";

    private String id;
    private String culture;
    private String provider;
    private String externalId;
    private Intention[] intentions;
    private Entity[] entities;
    private Date storageDate;
    private Date publishDate;
    private Date trainingDate;
    private URI apiUri;

    public Model() {
        super(MediaType.parse(MIME_TYPE));
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCulture() {
        return culture;
    }

    public void setCulture(String culture) {
        this.culture = culture;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Intention[] getIntentions() {
        return intentions;
    }

    public void setIntentions(Intention[] intentions) {
        this.intentions = intentions;
    }

    public Entity[] getEntities() {
        return entities;
    }

    public void setEntities(Entity[] entities) {
        this.entities = entities;
    }

    public Date getStorageDate() {
        return storageDate;
    }

    public void setStorageDate(Date storageDate) {
        this.storageDate = storageDate;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Date getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(Date trainingDate) {
        this.trainingDate = trainingDate;
    }

    public URI getApiUri() {
        return apiUri;
    }

    public void setApiUri(URI apiUri) {
        this.apiUri = apiUri;
    }
}