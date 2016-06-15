package org.limeprotocol.messaging.contents;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents an invoice for requesting a payment.
 */
public class Invoice extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.lime.invoice+json";

    private String number;
    private Date created;
    private Date dueTo;
    private PaymentMethod[] methods;
    private String currency;
    private BigDecimal taxes;
    private BigDecimal total;
    private InvoiceItem[] items;

    public Invoice() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets the invoice number.
     * @return
     */
    public String getNumber() {
        return number;
    }

    /**
     *  Sets the invoice number.
     * @param number
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Gets the invoice creation date.
     * @return
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Sets the invoice creation date.
     * @param created
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * Gets the invoice expiration.
     * @return
     */
    public Date getDueTo() {
        return dueTo;
    }

    /**
     * Sets the invoice expiration.
     * @param dueTo
     */
    public void setDueTo(Date dueTo) {
        this.dueTo = dueTo;
    }

    /**
     * Gets the seller supported methods for payment of the invoice.
     * @return
     */
    public PaymentMethod[] getMethods() {
        return methods;
    }

    /**
     * Sets the seller supported methods for payment of the invoice.
     * @param methods
     */
    public void setMethods(PaymentMethod[] methods) {
        this.methods = methods;
    }

    /**
     * Gets the invoice currency code related to the values.
     * @return
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the invoice currency code related to the values.
     * @param currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the invoice total taxes value.
     * @return
     */
    public BigDecimal getTaxes() {
        return taxes;
    }

    /**
     * Sets the invoice total taxes value.
     * @param taxes
     */
    public void setTaxes(BigDecimal taxes) {
        this.taxes = taxes;
    }

    /**
     * Gets the invoice total value, including taxes.
     * @return
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Sets the invoice total value, including taxes.
     * @param total
     */
    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    /**
     * Gets the invoice items.
     * @return
     */
    public InvoiceItem[] getItems() {
        return items;
    }

    /**
     * Sets the invoice items.
     * @param items
     */
    public void setItems(InvoiceItem[] items) {
        this.items = items;
    }
}
