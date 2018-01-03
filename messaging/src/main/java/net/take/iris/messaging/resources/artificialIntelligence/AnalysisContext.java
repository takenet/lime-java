package net.take.iris.messaging.resources.artificialIntelligence;

public class AnalysisContext {

    private String id;
    private Intention[] intentions;
    private Entity[] entities;

    public AnalysisContext() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
