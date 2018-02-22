package net.take.iris.messaging.resources.artificialIntelligence;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

import java.util.Date;
import java.util.Objects;

public class Entity extends DocumentBase {
    public static String MIME_TYPE = "application/vnd.iris.ai.entity+json";

    private String id;
    private String name;
    private Date storageDate;
    private EntityValues[] values;

    public Entity() {
        super(MediaType.parse(MIME_TYPE));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStorageDate() {
        return storageDate;
    }

    public void setStorageDate(Date storageDate) {
        this.storageDate = storageDate;
    }

    public EntityValues[] getValues() {
        return values;
    }

    public void setValues(EntityValues[] values) {
        this.values = values;
    }


}
