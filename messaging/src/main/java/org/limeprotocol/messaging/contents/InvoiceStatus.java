package org.limeprotocol.messaging.contents;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

import java.util.Date;

/**
 * Represents the status of the payment of an invoice.
 */
public class InvoiceStatus extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.lime.invoice-status+json";

    private InvoiceStatusStatus status;
    private Date date;
    private String code;

    public InvoiceStatus() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets the current invoice status.
     * @return
     */
    public InvoiceStatusStatus getStatus() {
        return status;
    }

    /**
     *  Sets the current invoice status.
     * @param status
     */
    public void setStatus(InvoiceStatusStatus status) {
        this.status = status;
    }

    /**
     * Gets the status date.
     * @return
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the status date.
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the status transaction code.
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the status transaction code.
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Defines the possible invoice payment status values.
     */
    public enum InvoiceStatusStatus {
        /**
         * Indicates that the payment operation was complete.
         */
        COMPLETED,

        /**
         * Indicates that the payment operation was cancelled by any of the parties.
         */
        CANCELLED,

        /**
         * Indicates that a previously completed payment operation was refunded to the payer.
         */
        REFUNDED,
    }
}
