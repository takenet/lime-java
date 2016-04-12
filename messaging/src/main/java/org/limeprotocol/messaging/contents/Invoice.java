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
    private String terms;
    private String account;
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
     * Gets the invoice payment terms description.
     * @return
     */
    public String getTerms() {
        return terms;
    }

    /**
     * Sets the invoice payment terms description.
     * @param terms
     */
    public void setTerms(String terms) {
        this.terms = terms;
    }

    /**
     * Gets the account information for the payment of the invoice.
     * @return
     */
    public String getAccount() {
        return account;
    }

    /**
     * Sets the account information for the payment of the invoice.
     * @param account
     */
    public void setAccount(String account) {
        this.account = account;
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

    /**
     * Defines a invoice item.
     */
    public class InvoiceItem {
        private BigDecimal quantity;
        private String description;
        private String currency;
        private BigDecimal unit;
        private BigDecimal taxes;
        private BigDecimal total;

        /**
         * Gets the item quantity.
         * @return
         */
        public BigDecimal getQuantity() {
            return quantity;
        }

        /**
         * Sets the item quantity.
         * @param quantity
         */
        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }

        /**
         * Gets the item description.
         * @return
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the item description.
         * @param description
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * Gets the item currency code related to the values.
         * @return
         */
        public String getCurrency() {
            return currency;
        }

        /**
         * Sets the item currency code related to the values.
         * @param currency
         */
        public void setCurrency(String currency) {
            this.currency = currency;
        }

        /**
         * Gets the unit value of each item.
         * @return
         */
        public BigDecimal getUnit() {
            return unit;
        }

        /**
         * Sets the unit value of each item.
         * @param unit
         */
        public void setUnit(BigDecimal unit) {
            this.unit = unit;
        }

        /**
         * Gets the taxes values for the item.
         * @return
         */
        public BigDecimal getTaxes() {
            return taxes;
        }

        /**
         * Sets the taxes values for the item.
         * @param taxes
         */
        public void setTaxes(BigDecimal taxes) {
            this.taxes = taxes;
        }

        /**
         * Gets the total value of the items, including taxes.
         * @return
         */
        public BigDecimal getTotal() {
            return total;
        }

        /**
         * Sets the total value of the items, including taxes.
         * @param total
         */
        public void setTotal(BigDecimal total) {
            this.total = total;
        }
    }
}
