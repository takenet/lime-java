package org.limeprotocol.messaging.resource;

import org.limeprotocol.Document;
import org.limeprotocol.MediaType;

public class Account implements Document {

    public final String RESOURCE_PATH = "/account";

    public final String MIME_TYPE = "application/vnd.lime.account+json";

    public final String FULL_NAME_KEY = "fullName";
    public final String ADDRESS_KEY = "address";
    public final String CITY_KEY = "city";
    public final String EMAIL_KEY = "email";
    public final String PHONE_NUMBER_KEY = "phoneNumber";
    public final String CELL_PHONE_NUMBER_KEY = "cellPhoneNumber";
    public final String IS_TEMPORARY_KEY = "isTemporary";
    public final String PASSWORD_KEY = "password";
    public final String OLD_PASSWORD_KEY = "oldPassword";
    public final String INBOX_SIZE_KEY = "inboxSize";
    public final String ALLOW_ANONYMOUS_SENDER_KEY = "allowAnonymousSender";
    public final String ALLOW_UNKNOWN_SENDER_KEY = "allowUnknownSender";
    public final String STORE_MESSAGE_CONTENT_KEY = "storeMessageContent";

    private MediaType mediaType;

    public Account() {
        this.mediaType = MediaType.parse(MIME_TYPE);
    }

    /**
     * The user full name
     */
    public String fullName;

    /**
     * The user address
     */

    public String address;

    /**
     * The user city
     */
    public String city;

    /**
     * The user e-mail address
     */
    public String email;

    /**
     * The user phone number
     */
    public String phoneNumber;

    /**
     * The user cellphone number
     */
    public String cellPhoneNumber;

    /**
     * Indicates that the account is
     * temporary is valid only in
     * the current session
     */
    public Boolean isTemporary;


    /**
     * Size of account inbox
     * for storing offline messages
     */
    public Integer inboxSize;

    /**
     * Indicates if this account
     * allows receive messages from
     * anonymous users
     */
    public Boolean allowAnonymousSender;

    /**
     * Indicates if this account
     * allows receive messages from
     * users that are not in
     * the account contact list
     */
    public Boolean allowUnknownSender;

    /**
     * Indicates if the content of messages
     * from this account should be stored in
     * the server. Note that for offline messages,
     * this will always happens.
     */
    public Boolean storeMessageContent;

    public Boolean getAllowAnonymousSender() {
        return allowAnonymousSender;
    }

    public void setAllowAnonymousSender(Boolean allowAnonymousSender) {
        this.allowAnonymousSender = allowAnonymousSender;
    }

    public Boolean getAllowUnknownSender() {
        return allowUnknownSender;
    }

    public void setAllowUnknownSender(Boolean allowUnknownSender) {
        this.allowUnknownSender = allowUnknownSender;
    }

    public Boolean isTemporary() {
        return isTemporary;
    }

    public void setIsTemporary(Boolean isTemporary) {
        this.isTemporary = isTemporary;
    }

    public Boolean getStoreMessageContent() {
        return storeMessageContent;
    }

    public void setStoreMessageContent(Boolean storeMessageContent) {
        this.storeMessageContent = storeMessageContent;
    }

    public Integer getInboxSize() {
        return inboxSize;
    }

    public void setInboxSize(Integer inboxSize) {
        this.inboxSize = inboxSize;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public MediaType getMediaType() {
        return this.mediaType;
    }
}