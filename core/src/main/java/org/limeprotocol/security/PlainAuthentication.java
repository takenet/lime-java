package org.limeprotocol.security;

import org.limeprotocol.util.StringUtils;

public class PlainAuthentication extends Authentication {
    private String password;

    public PlainAuthentication() {
        super(AuthenticationScheme.PLAIN);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setToBase64Password(String password) {
        if (StringUtils.isNullOrWhiteSpace(password)) {
            setPassword(password);
        } else {
            setPassword(StringUtils.toBase64(password));
        }
    }
}
