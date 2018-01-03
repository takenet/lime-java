package net.take.iris.messaging.resources;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.Identity;
import org.limeprotocol.MediaType;

import java.util.Date;
import java.util.Map;

public class EventTrack extends DocumentBase {
    public static final String MIME_TYPE = "application/vnd.iris.eventTrack+json";

    private Identity ownerIdentity;
    private Identity identity;
    private Date storageDate;
    private String category;
    private String action;
    private Map<String, String> extras;
    private Integer count;

    public EventTrack() {
        super(MediaType.parse(MIME_TYPE));
    }


    public Identity getOwnerIdentity() {
        return ownerIdentity;
    }

    public void setOwnerIdentity(Identity ownerIdentity) {
        this.ownerIdentity = ownerIdentity;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public Date getStorageDate() {
        return storageDate;
    }

    public void setStorageDate(Date storageDate) {
        this.storageDate = storageDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}