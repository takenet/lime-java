package org.limeprotocol;

import org.limeprotocol.Exceptions.ArgumentNullException;
import org.limeprotocol.Util.StringUtils;

public class Identity {
    private String name;
    private String domain;

    public  Identity(String name, String domain){
        this.name = name;
        this.domain = domain;
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
            throw new ArgumentNullException("s");
        }

        String[] splittedIdentity = s.split("@");

        String name = !StringUtils.isNullOrWhiteSpace(splittedIdentity[0]) ? splittedIdentity[0] : null;
        String domain = splittedIdentity.length > 1 ? splittedIdentity[1] : null;

        return new Identity(name, domain);
    }
}
