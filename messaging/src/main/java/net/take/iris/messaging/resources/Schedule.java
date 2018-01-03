package net.take.iris.messaging.resources;

import org.limeprotocol.DocumentBase;

import org.limeprotocol.MediaType;
import org.limeprotocol.Message;

import java.util.Date;

public class Schedule extends DocumentBase {
    public static final String MIME_TYPE = "application/vnd.iris.schedule+json";

    private Date when;
    private Message message;
    private ScheduleStatus status;

    public Schedule() {
        super(MediaType.parse(MIME_TYPE));
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleStatus status) {
        this.status = status;
    }


    public enum ScheduleStatus {
        SCHEDULED,
        EXECUTED,
        CANCELED
    }
}
