package org.limeprotocol;

import org.limeprotocol.util.StringUtils;

public class Reason {
    public final String CODE_KEY = "code";
    public final String DESCRIPTION_KEY = "description";

    private int code;
    private String description;

    @Override
    public String toString() {
        return StringUtils.format("{0} (Code {1})", description, code);
    }
}
