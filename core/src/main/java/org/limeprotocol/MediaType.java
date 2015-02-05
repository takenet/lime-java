package org.limeprotocol;

import org.limeprotocol.util.Cast;
import org.limeprotocol.util.StringUtils;

public class MediaType {

    /**
     * The top-level type
     * identifier. The valid values
     * are text, application, image,
     * audio and video.
     */
    private String type;

    private String subtype;

    /**
     * Media type suffix
     * @link  href="http://tools.ietf.org/html/rfc6839"
     */
    private String suffix;

    /**
     * Indicates if the MIME
     * represents a JSON type
     */
    private boolean isJson;

    public MediaType() {

    }

    public MediaType(String type, String subtype) {
        this(type, subtype, null);
    }

    public MediaType(String type, String subtype, String suffix) {
        if (StringUtils.isNullOrWhiteSpace(type)) {
            throw new IllegalArgumentException("type");
        }

        this.type = type;

        if (StringUtils.isNullOrWhiteSpace(subtype)) {
            throw new IllegalArgumentException("subtype");
        }

        this.subtype = subtype;

        this.suffix = suffix;
    }

    public boolean isJson() {
        return (suffix != null && suffix.equalsIgnoreCase(SubTypes.JSON)) ||
                (subtype != null && subtype.equalsIgnoreCase(SubTypes.JSON));
    }

    public String getSuffix() {
        return suffix;
    }

    /**
     * Returns a <see cref="System.String" /> that represents this instance.
     **/
    @Override
    public String toString() {
        if (StringUtils.isNullOrWhiteSpace(this.suffix)) {
            return StringUtils.format("{0}/{1}", this.type, this.subtype);
        } else {
            return String.format("{0}/{1}+{2}", this.type, this.subtype, this.suffix);
        }
    }

    /**
     * Returns a hash code for this instance.
     * @return A hash code for this instance, suitable for use in hashing algorithms and data structures like a hash table.
     */
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /**
     * Determines whether the specified <see cref="System.Object" }, is equal to this instance.
     *
     * @param obj
     * @return <c>true</c> if the specified <see cref="System.Object" /> is equal to this instance; otherwise, <c>false</c>.
     */
    @Override
    public boolean equals(Object obj) {
        MediaType mediaType = Cast.as(MediaType.class, obj);

        if (mediaType == null) {
            return false;
        }

        return this.type.equalsIgnoreCase(mediaType.type) &&
                this.subtype.equalsIgnoreCase(mediaType.subtype) &&
                (this.suffix == null && mediaType.suffix == null || (this.suffix != null && mediaType.suffix != null && this.suffix.equalsIgnoreCase(mediaType.suffix)));
    }

    /**
     * Parses the String to a MediaType object.
     * @param s
     */
    public static MediaType parse(String s) {
        if (StringUtils.isNullOrWhiteSpace(s)) {
            throw new IllegalArgumentException("value");
        }

        String[] splittedMediaType = s.split("/");

        if (splittedMediaType.length != 2) {
            throw new IllegalArgumentException("Invalid media type format");
        }

        String type = splittedMediaType[0];

        String[] splittedSubtype = splittedMediaType[1].split("\\+");

        String subtype = splittedSubtype[0];

        String suffix = null;

        if (splittedSubtype.length > 1) {
            suffix = splittedSubtype[1];
        }

        return new MediaType(type, subtype, suffix);
    }

    /**
     * Try parses the String to a MediaType object.
     *
     * @param s
     * @param mediaType
     */
    ///WARNING: Remember that Java hasn't out-operator!
    ///TODO: Check if all call to this method execute parser again
    public static boolean tryParse(String s, MediaType mediaType) {
        try {
            MediaType.parse(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static class DiscreteTypes {
        public static String Application = "application";

        public static String Text = "text";

        public static String Image = "image";

        public static String Audio = "audio";

        public static String Video = "video";
    }

    public static class CompositeTypes {
        public static String Message = "message";

        public static String Multipart = "multipart";
    }

    public static class SubTypes {
        public static String Plain = "plain";

        public static String JSON = "json";

        public static String XML = "xml";

        public static String HTML = "html";

        public static String JPeg = "jpeg";

        public static String Bitmap = "bmp";

        public static String Javascript = "javascript";
    }

}
