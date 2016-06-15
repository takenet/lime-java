package org.limeprotocol.messaging.contents;

import java.math.BigDecimal;

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