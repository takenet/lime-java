package org.limeprotocol.messaging.contents;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

/**
 * Represents a geographic location information.
 */
public class Location extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.lime.location+json";

    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Integer course;
    private Double speed;
    private Double accuracy;

    public Location() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets the the latitude, in degrees.
     * Latitude can range from -90.0 to 90.0. Latitude is measured in degrees north or south from the equator. Positive values are north of the equator and negative values are south of the equator.
     * @return
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the the latitude, in degrees.
     * Latitude can range from -90.0 to 90.0. Latitude is measured in degrees north or south from the equator. Positive values are north of the equator and negative values are south of the equator.
     * @param latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude, in degrees.
     * The longitude can range from -180.0 to 180.0. Longitude is measured in degrees east or west of the prime meridian. Negative values are west of the prime meridian, and positive values are east of the prime meridian.
     * @return
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude, in degrees.
     * The longitude can range from -180.0 to 180.0. Longitude is measured in degrees east or west of the prime meridian. Negative values are west of the prime meridian, and positive values are east of the prime meridian.
     * @param longitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the altitude, in meters.
     * @return
     */
    public Double getAltitude() {
        return altitude;
    }

    /**
     * Sets the altitude, in meters.
     * @param altitude
     */
    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    /**
     * Gets the course, in degrees.
     * The course can range from 0 to 360.
     * @return
     */
    public Integer getCourse() {
        return course;
    }

    /**
     * Sets the course, in degrees.
     * The course can range from 0 to 360.
     * @param course
     */
    public void setCourse(Integer course) {
        this.course = course;
    }

    /**
     * Gets the speed, in meters per second.
     * @return
     */
    public Double getSpeed() {
        return speed;
    }

    /**
     * Sets the speed, in meters per second.
     * @param speed
     */
    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    /**
     * Gets the location accuracy, in meters.
     * @return
     */
    public Double getAccuracy() {
        return accuracy;
    }

    /**
     * Sets the location accuracy, in meters.
     * @param accuracy
     */
    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }
}
