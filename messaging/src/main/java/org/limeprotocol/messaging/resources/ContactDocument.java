package org.limeprotocol.messaging.resources;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

import java.net.URI;
import java.util.Map;

public abstract class ContactDocument extends DocumentBase {

    private String address;
    private String city;
    private String email;
    private String phoneNumber;
    private URI photoUri;
    private String cellPhoneNumber;
    private Gender gender;
    private Integer timezone;
    private String culture;
    private Map<String, String> extras;

    ContactDocument(MediaType mediaType) {
        super(mediaType);
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
     * Represents the person account gender.
     * @return
     */
    public Account.Gender getGender() {
        return gender;
    }

    /**
     * Represents the person account gender.
     * @param gender
     */
    public void setGender(Account.Gender gender) {
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
     * Gets the contact extra information.
     * @return
     */
    public Map<String, String> getExtras() {
        return extras;
    }

    /**
     * Sets the contact extra information.
     * @param extras
     */
    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
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
