package org.limeprotocol.messaging.resources;

import org.limeprotocol.Document;
import org.limeprotocol.DocumentBase;
import org.limeprotocol.Identity;
import org.limeprotocol.MediaType;

import java.net.URI;

public class Account extends DocumentBase {

    public static final String RESOURCE_PATH = "/account";

    public static final String MIME_TYPE = "application/vnd.lime.account+json";

    public Account() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * The user full name
     */
    private String fullName;

    /**
     * The user address
     */
    private String address;

    /**
     * The user city
     */
    private String city;

    /**
     * The user e-mail address
     */
    private String email;

    /**
     * The user phone number
     */
    private String phoneNumber;

    /**
     * The user photo URI
     */
    private URI photoUri;

    /**
     * The user cellphone number
     */
    private String cellPhoneNumber;

    /**
     * Indicates that the account is
     * temporary is valid only in
     * the current session
     */
    private Boolean isTemporary;

    /**
     * Base64 representation of the account password.
     */
    private String password;

    /**
     * Base64 representation of the account password.
     * Mandatory in case of updating account password.
     */
    private String oldPassword;

    /**
     * Access key for updating the account without knowing the old password.
     */
    private String accessKey;

    /**
     * Size of account inbox
     * for storing offline messages
     */
    private Integer inboxSize;

    /**
     * Indicates if this account
     * allows receive messages from
     * anonymous users
     */
    private Boolean allowAnonymousSender;

    /**
     * Indicates if this account
     * allows receive messages from
     * users that are not in
     * the account contact list
     */
    private Boolean allowUnknownSender;

    /**
     * Indicates if the content of messages
     * from this account should be stored in
     * the server. Note that for offline messages,
     * this will always happens.
     */
    private Boolean storeMessageContent;

    /**
     * Alternative account address.
     */
    private Identity alternativeAccount;

    /**
     * Indicates if the account info should be published to the domain directory.
     */
    private Boolean publishToDirectory;

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

    public Boolean getIsTemporary() {
        return isTemporary;
    }

    public void setIsTemporary(Boolean isTemporary) {
        this.isTemporary = isTemporary;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
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

    public URI getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(URI photoUri) {
        this.photoUri = photoUri;
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

    public Identity getAlternativeAccount() {
        return alternativeAccount;
    }

    public void setAlternativeAccount(Identity alternativeAccount) {
        this.alternativeAccount = alternativeAccount;
    }

    public Boolean getPublishToDirectory() {
        return publishToDirectory;
    }

    public void setPublishToDirectory(Boolean publishToDirectory) {
        this.publishToDirectory = publishToDirectory;
    }
}