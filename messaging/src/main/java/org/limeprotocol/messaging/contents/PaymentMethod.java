package org.limeprotocol.messaging.contents;

/**
 * Defines a payment method.
 */
public class PaymentMethod {

    private String name;
    private String account;

    /**
     * Gets the payment method name.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the payment method name.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the payment method account information of the seller.
     * @return
     */
    public String getAccount() {
        return account;
    }

    /**
     * Sets the payment method account information of the seller.
     * @param account
     */
    public void setAccount(String account) {
        this.account = account;
    }
}