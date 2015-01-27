package org.limeprotocol;

import org.limeprotocol.util.StringUtils;

public class Reason {
    private int code;
    private String description;

    public Reason(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return StringUtils.format("{0} (Code {1})", description, code);
    }
}
