package ai.blip.messaging.resources.artificialIntelligence;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

import java.util.Date;
import java.util.Objects;

public class Entity extends DocumentBase {
    public static String MIME_TYPE = "application/vnd.iris.ai.entity+json";

    /**
     * Initializes a new instance of the @Entity class.
     */
    public Entity() {
        super(MediaType.parse(MIME_TYPE));
    }

    private String id;
    private String name;
    private Date storageDate;
    private String jsonValues;
    private EntityValues[] values;
    private boolean isDeleted;

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

    public String getJsonValues() {
        return jsonValues;
    }

    public void setJsonValues(String jsonValues) {
        this.jsonValues = jsonValues;
    }

    public EntityValues[] getValues() {
        return values;
    }

    public void setValues(EntityValues[] values) {
        this.values = values;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Entity)) return false;
        Entity entity = (Entity) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        int result = id == null || id.isEmpty() ? 0 : id.hashCode();
        return result;
    }

    public class EntityValues {
        private String name;
        private String[] synonymous;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
