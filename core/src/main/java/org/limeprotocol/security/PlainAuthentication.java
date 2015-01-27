package org.limeprotocol.security;

import org.limeprotocol.util.StringUtils;
import org.apache.commons.codec.binary.Base64;

public class PlainAuthentication extends Authentication {
    private String password;

    public PlainAuthentication() {
        super(AuthenticationScheme.Plain);
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
            setPassword(new String(Base64.encodeBase64(password.getBytes())));
        }
    }
}
