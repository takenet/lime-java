package org.limeprotocol;

import java.net.URI;

import org.limeprotocol.util.Cast;
import org.limeprotocol.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * Represents a URI from the lime scheme.
 */
public final class LimeUri {
    private URI absoluteUri;
    public static final String LIME_URI_SCHEME = "lime";

    public LimeUri(String uriPath) {
        if (StringUtils.isNullOrWhiteSpace(uriPath)) {
            throw new IllegalArgumentException("uriPath");
        }

        try {
            absoluteUri = new URI(uriPath);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI format");
        }

        if (absoluteUri.isAbsolute() && !absoluteUri.getScheme().equals(LIME_URI_SCHEME)) {
            throw new IllegalArgumentException(String.format("Invalid URI scheme. Expected is '%1$s'", LIME_URI_SCHEME));
        }

        this.path = StringUtils.trimEnd(uriPath, "/");
    }

    private String path;

    public boolean isRelative() {
        return !absoluteUri.isAbsolute();
    }

    public String getPath() {
        return path;
    }

    /**
     * Convert the current absolute path to a Uri.
     */
    public URI toUri() {
        if (absoluteUri != null && !absoluteUri.isAbsolute()) {
            throw new IllegalStateException("The URI path is relative");
        }

        return absoluteUri;
    }

    public URI toUri(Identity authority) {
        if (absoluteUri != null && absoluteUri.isAbsolute()) {
            throw new IllegalStateException("The URI path is absolute");
        }

        if (authority == null) {
            throw new IllegalStateException("authority");
        }

        try {
            URI baseUri = getBaseUri(authority);
            return baseUri.resolve(getPath());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e.getMessage());
        }
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

    public static URI getBaseUri(Identity authority) throws MalformedURLException {
        return URI.create(StringUtils.format("{0}://{1}/", LIME_URI_SCHEME, authority));
    }
}