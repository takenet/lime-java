package org.limeprotocol.security;

import org.limeprotocol.util.StringUtils;

public class KeyAuthentication extends Authentication {
    private String key;

    public KeyAuthentication() {
        super(AuthenticationScheme.KEY);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setToBase64Password(String key) {
        if (StringUtils.isNullOrWhiteSpace(key)) {
            setKey(key);
        } else {
            setKey(StringUtils.toBase64(key));
        }
    }
}
