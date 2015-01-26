package org.limeprotocol;

import com.sun.jndi.toolkit.url.Uri;
import org.limeprotocol.util.Cast;
import org.limeprotocol.util.StringUtils;

import java.net.MalformedURLException;

/// <summary>
/// Represents a URI
/// from the lime scheme.
/// </summary>
public final class LimeUri {
    private Uri absoluteUri;
    public static final String LIME_URI_SCHEME = "lime";

    //TODO: Parse comments methods of Uri to JAVA
    public LimeUri(String uriPath) {
        if (StringUtils.isNullOrWhiteSpace(uriPath)) {
            throw new IllegalArgumentException("uriPath");
        }

//        if (Uri.IsWellFormedUriString(uriPath, UriKind.Absolute)) {
//            absoluteUri = new Uri(uriPath);
//            // In Linux, a path like '/presence' is considered
//            // a valid absolute file uri
//
//            if (absoluteUri.IsFile) {
//                absoluteUri = null;
//            } else if (!absoluteUri.Scheme.Equals(LIME_URI_SCHEME)) {
//                throw new ArgumentException(string.Format("Invalid URI scheme. Expected is '{0}'", LIME_URI_SCHEME));
//            }
//        } else if (!Uri.IsWellFormedUriString(uriPath, UriKind.Relative)) {
//            throw new ArgumentException("Invalid URI format");
//        }

        this.path = StringUtils.trimEnd(uriPath, "/");
    }

    /// <summary>
    /// Fragment or complete
    /// URI path.
    /// </summary>
    private String path;

    /// <summary>
    /// Indicates if the path
    /// is relative.
    /// </summary>
    public boolean isRelative;

    public boolean isRelative() {
        return absoluteUri == null;
    }

    public String getPath() {
        return path;
    }

    /// <summary>
    /// Convert the current
    /// absolute path to a Uri.
    /// </summary>
    /// <returns></returns>
    public Uri toUri() throws IllegalStateException {
        if (absoluteUri == null) {
            throw new IllegalStateException("The URI path is relative");
        }

        return absoluteUri;
    }

    /// <summary>
    /// Convert the relative
    /// path to a Uri, using
    /// the identity as the
    /// URI authority.
    /// </summary>
    /// <param name="authority"></param>
    /// <returns></returns>
    //TODO: Parse comments methods of Uri to JAVA
    public Uri toUri(Identity authority) throws MalformedURLException, IllegalStateException {
        if (absoluteUri != null) {
            throw new IllegalStateException("The URI path is absolute");
        }

        if (authority == null) {
            throw new IllegalArgumentException("authority");
        }

        Uri baseUri = getBaseUri(authority);
        //return new Uri(baseUri, getPath());
        return new Uri(getPath());
    }

    @Override
    public int hashCode() {
        //TODO: Check if toLowerCase is the same that ToLowerCaseIgnoreCase
        return this.toString().toLowerCase().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        LimeUri limeUri = Cast.as(LimeUri.class, obj);
        if (limeUri == null) {
            return false;
        }

        return this.path.equalsIgnoreCase(limeUri.getPath());
    }

    @Override
    public String toString() {
        return this.path;
    }

    public static LimeUri parse(String value) {
        return new LimeUri(value);
    }

    public static Uri getBaseUri(Identity authority) throws MalformedURLException {
        return new Uri(StringUtils.format("{0}://{1}/", LIME_URI_SCHEME, authority));
    }
}