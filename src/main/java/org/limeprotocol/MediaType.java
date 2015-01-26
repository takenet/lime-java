package org.limeprotocol;

import org.limeprotocol.util.Cast;
import org.limeprotocol.util.StringUtils;

public class MediaType {

    /// <summary>
    /// The top-level type
    /// identifier. The valid values
    /// are text, application, image,
    /// audio and video.
    /// </summary>
    private String Type;

    private String Subtype;

    /// <summary>
    /// Media type suffix
    /// </summary>
    /// <a href="http://tools.ietf.org/html/rfc6839"/>
    private String Suffix;

    /// <summary>
    /// Indicates if the MIME
    /// represents a JSON type
    /// </summary>
    private boolean isJson;

    public MediaType() {

    }

    public MediaType(String type, String subtype, String suffix) {
        if (StringUtils.isNullOrWhiteSpace(type)) {
            throw new IllegalArgumentException("type");
        }

        this.Type = type;

        if (StringUtils.isNullOrWhiteSpace(subtype)) {
            throw new IllegalArgumentException("subtype");
        }

        this.Subtype = subtype;

        this.Suffix = suffix;
    }

    public boolean isJson() {
        return (Suffix != null && Suffix.equalsIgnoreCase(SubTypes.JSON)) ||
                (Subtype != null && Subtype.equalsIgnoreCase(SubTypes.JSON));
    }

    public String getSuffix() {
        return Suffix;
    }

    /// <summary>
    /// Returns a <see cref="System.String" /> that represents this instance.
    /// </summary>
    /// <returns>
    /// A <c cref="System.String" /> that represents this instance.
    /// </returns>
    @Override
    public String toString() {
        if (StringUtils.isNullOrWhiteSpace(this.Suffix)) {
            return StringUtils.format("{0}/{1}", this.Type, this.Subtype);
        } else {
            return String.format("{0}/{1}+{2}", this.Type, this.Subtype, this.Suffix);
        }
    }

    /// <summary>
    /// Returns a hash code for this instance.
    /// </summary>
    /// <returns>
    /// A hash code for this instance, suitable for use in hashing algorithms and data structures like a hash table.
    /// </returns>
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /// <summary>
    /// Determines whether the specified <see cref="System.Object" }, is equal to this instance.
    /// </summary>
    /// <param name="obj">The <see cref="System.Object" /> to compare with this instance.</param>
    /// <returns>
    ///   <c>true</c> if the specified <see cref="System.Object" /> is equal to this instance; otherwise, <c>false</c>.
    /// </returns>
    @Override
    public boolean equals(Object obj) {
        MediaType mediaType = Cast.as(MediaType.class, obj);

        if (mediaType == null) {
            return false;
        }

        return this.Type.equalsIgnoreCase(mediaType.Type) &&
                this.Subtype.equalsIgnoreCase(mediaType.Subtype) &&
                (this.Suffix == null && mediaType.Suffix == null || (this.Suffix != null && mediaType.Suffix != null && this.Suffix.equalsIgnoreCase(mediaType.Suffix)));
    }

    /// <summary>
    /// Parses the String to a MediaType object.
    /// </summary>
    /// <param name="s">The String.</param>
    /// <returns></returns>
    /// <exception cref="System.ArgumentNullException">value</exception>
    /// <exception cref="System.FormatException">Invalid media type format</exception>
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

    /// <summary>
    /// Try parses the String to a MediaType object.
    /// </summary>
    /// <param name="s">The String.</param>
    /// <param name="mediaType">Type of the media.</param>
    /// <returns></returns>
    /// WARNING: Remember that Java hasn't out-operator!
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
