package net.take.iris.messaging.resources;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.Identity;
import org.limeprotocol.MediaType;

public class DistributionList extends DocumentBase {
    public static final String MIME_TYPE = "application/vnd.iris.distribution-list+json";

    private Identity identity;

    public DistributionList() {
        super(MediaType.parse(MIME_TYPE));
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
