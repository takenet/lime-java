package org.limeprotocol.messaging.resources;

import org.limeprotocol.Document;
import org.limeprotocol.DocumentBase;
import org.limeprotocol.Identity;
import org.limeprotocol.MediaType;

import java.net.URI;

public class Account extends ContactDocument {

    public static final String RESOURCE_PATH = "/account";

    public static final String MIME_TYPE = "application/vnd.lime.account+json";

    public Account() {
        super(MediaType.parse(MIME_TYPE));
    }

    private String fullName;
    private Boolean isTemporary;
    private String password;
    private String oldPassword;
    private Integer inboxSize;
    private Boolean allowAnonymousSender;
    private Boolean allowUnknownSender;
    private Boolean storeMessageContent;
    private Boolean encryptMessageContent;
    private String accessKey;
    private Identity alternativeAccount;
    private Boolean publishToDirectory;

    /**
     * The user full name.
     * @return
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * The user full name.
     * @param fullName
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Indicates that the account is temporary is valid only in the current session.
     * @return
     */
    public Boolean getIsTemporary() {
        return isTemporary;
    }

    /**
     * Indicates that the account is temporary is valid only in the current session.
     * @param temporary
     */
    public void setIsTemporary(Boolean temporary) {
        isTemporary = temporary;
    }

    /**
     * Base64 representation of the account password.
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Base64 representation of the account password.
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Base64 representation of the account password.
     * Mandatory in case of updating account password.
     * @return
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * Base64 representation of the account password.
     * Mandatory in case of updating account password.
     * @param oldPassword
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * Access key for updating the account without knowing the old password.
     * @return
     */
    public String getAccessKey() {
        return accessKey;
    }

    /**
     * Access key for updating the account without knowing the old password.
     * @param accessKey
     */
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * Size of account inbox for storing offline messages.
     * @return
     */
    public Integer getInboxSize() {
        return inboxSize;
    }

    /**
     * Size of account inbox for storing offline messages.
     * @param inboxSize
     */
    public void setInboxSize(Integer inboxSize) {
        this.inboxSize = inboxSize;
    }

    /**
     * Indicates if this account allows receive messages from anonymous users.
     * @return
     */
    public Boolean getAllowAnonymousSender() {
        return allowAnonymousSender;
    }

    /**
     * Indicates if this account allows receive messages from anonymous users.
     * @param allowAnonymousSender
     */
    public void setAllowAnonymousSender(Boolean allowAnonymousSender) {
        this.allowAnonymousSender = allowAnonymousSender;
    }

    /**
     * Indicates if this account allows receive messages from users that are not in the account contact list.
     * @return
     */
    public Boolean getAllowUnknownSender() {
        return allowUnknownSender;
    }

    /**
     * Indicates if this account allows receive messages from users that are not in the account contact list.
     * @param allowUnknownSender
     */
    public void setAllowUnknownSender(Boolean allowUnknownSender) {
        this.allowUnknownSender = allowUnknownSender;
    }

    /**
     * Indicates if the content of messages from this account should be stored in the server.
     * Note that for offline messages, this will always happens.
     * @return
     */
    public Boolean getStoreMessageContent() {
        return storeMessageContent;
    }

    /**
     * Indicates if the content of messages from this account should be stored in the server.
     * Note that for offline messages, this will always happens.
     * @param storeMessageContent
     */
    public void setStoreMessageContent(Boolean storeMessageContent) {
        this.storeMessageContent = storeMessageContent;
    }

    /**
     * Alternative account address.
     * @return
     */
    public Identity getAlternativeAccount() {
        return alternativeAccount;
    }

    /**
     * Alternative account address.
     * @param alternativeAccount
     */
    public void setAlternativeAccount(Identity alternativeAccount) {
        this.alternativeAccount = alternativeAccount;
    }

    /**
     * Indicates if the account info should be published to the domain directory.
     * @return
     */
    public Boolean getPublishToDirectory() {
        return publishToDirectory;
    }

    /**
     * Indicates if the account info should be published to the domain directory.
     * @param publishToDirectory
     */
    public void setPublishToDirectory(Boolean publishToDirectory) {
        this.publishToDirectory = publishToDirectory;
    }

    /**
     * Indicates if the content of messages from this account should be encrypted in the server.
     * @return
     */
    public Boolean getEncryptMessageContent() {
        return encryptMessageContent;
    }

    /**
     * Indicates if the content of messages from this account should be encrypted in the server.
     * @param encryptMessageContent
     */
    public void setEncryptMessageContent(Boolean encryptMessageContent) {
        this.encryptMessageContent = encryptMessageContent;
    }
}