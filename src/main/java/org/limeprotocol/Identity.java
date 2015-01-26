package org.limeprotocol;

import org.limeprotocol.util.Cast;
import org.limeprotocol.util.StringUtils;

public class Identity {
    private String name;
    private String domain;

    public  Identity(String name, String domain){
        setName(name);
        setDomain(domain);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public boolean equals(Object o) {
        Identity identity = Cast.as(Identity.class, o);

        if (identity == null) {
            return false;
        }

        return ((this.name == null && identity.name == null) || (this.name != null && this.name.equalsIgnoreCase(identity.name))) &&
                ((this.domain == null && identity.domain == null) || (this.domain != null && this.domain.equalsIgnoreCase(identity.domain)));
    }

    @Override
    public int hashCode() {
        return this.toString().toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        if (StringUtils.isNullOrWhiteSpace(this.domain)) {
            return this.name;
        }
        else {
            return String.format("%1$s@%2$s",
                                name == null ? "" : name,
                                domain);
        }
    }

    /// <summary>
    /// Parses the string to a valid Identity.
    /// </summary>
    /// <param name="s">The s.</param>
    /// <returns></returns>
    /// <exception cref="System.ArgumentNullException">s</exception>
    /// <exception cref="System.FormatException">Invalid identity format</exception>
    public static Identity parse(String s)
    {
        if (StringUtils.isNullOrWhiteSpace(s))
        {
            throw new IllegalArgumentException("s");
        }

        String[] splittedIdentity = s.split("@");

        String name = !StringUtils.isNullOrWhiteSpace(splittedIdentity[0]) ? splittedIdentity[0] : null;
        String domain = splittedIdentity.length > 1 ? splittedIdentity[1] : null;

        return new Identity(name, domain);
    }
}
