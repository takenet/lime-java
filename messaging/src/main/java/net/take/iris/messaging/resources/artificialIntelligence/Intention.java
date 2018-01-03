package net.take.iris.messaging.resources.artificialIntelligence;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

import java.util.Date;

public class Intention extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.iris.ai.intention+json";

    private String id;
    private String name;
    private Question[] questions;
    private Answer[] answers;
    private Date storageDate;

    public Intention() {
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

    public Question[] getQuestions() {
        return questions;
    }

    public void setQuestions(Question[] questions) {
        this.questions = questions;
    }

    public Answer[] getAnswers() {
        return answers;
    }

    public void setAnswers(Answer[] answers) {
        this.answers = answers;
    }

    public Date getStorageDate() {
        return storageDate;
    }

    public void setStorageDate(Date storageDate) {
        this.storageDate = storageDate;
    }
}
