package org.limeprotocol.security;

import org.limeprotocol.util.StringUtils;

public class ExternalAuthentication extends Authentication {
    private String token;
    private String issuer;

    public ExternalAuthentication() {
        super(AuthenticationScheme.EXTERNAL);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setToBase64Token(String password) {
        if (StringUtils.isNullOrWhiteSpace(password)) {
            setToken(password);
        } else {
            setToken(StringUtils.toBase64(password));
        }
    }
}
