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

    private String fullName;
    private String address;
    private String city;
    private String email;
    private String phoneNumber;
    private URI photoUri;
    private String cellPhoneNumber;
    private Boolean isTemporary;
    private String password;
    private String oldPassword;
    private String accessKey;
    private Integer inboxSize;
    private Boolean allowAnonymousSender;
    private Boolean allowUnknownSender;
    private Boolean storeMessageContent;
    private Identity alternativeAccount;
    private Boolean publishToDirectory;
    private Gender gender;
    private Integer timezone;
    private String culture;

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
     * The user street address.
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * The user street address.
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * The user city.
     * @return
     */
    public String getCity() {
        return city;
    }

    /**
     * The user city.
     * @param city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * The user e-mail address.
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * The user e-mail address.
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * The user phone number.
     * @return
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * The user phone number.
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * The user photo URI.
     * @return
     */
    public URI getPhotoUri() {
        return photoUri;
    }

    /**
     * The user photo URI.
     * @param photoUri
     */
    public void setPhotoUri(URI photoUri) {
        this.photoUri = photoUri;
    }

    /**
     * The user cellphone number.
     * @return
     */
    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    /**
     * The user cellphone number.
     * @param cellPhoneNumber
     */
    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
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
     * Represents the person account gender.
     * @return
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Represents the person account gender.
     * @param gender
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * Represents the account timezone relative to GMT.
     * @return
     */
    public Integer getTimezone() {
        return timezone;
    }

    /**
     * Represents the account timezone relative to GMT.
     * @param timezone
     */
    public void setTimezone(Integer timezone) {
        this.timezone = timezone;
    }

    /**
     * Represents the person account culture info, in the IETF language tag format.
     * https://en.wikipedia.org/wiki/IETF_language_ta
     * @return
     */
    public String getCulture() {
        return culture;
    }

    /**
     * Represents the person account culture info, in the IETF language tag format.
     * https://en.wikipedia.org/wiki/IETF_language_ta
     * @return
     */
    public void setCulture(String culture) {
        this.culture = culture;
    }



    /**
     * Represents the account person gender
     */
    public enum Gender {
        /**
         * The male gender.
         */
        MALE,
        /**
         * The female gender
         */
        FEMALE
    }
}