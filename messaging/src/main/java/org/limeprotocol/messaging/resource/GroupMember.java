package org.limeprotocol.messaging.resource;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.Identity;
import org.limeprotocol.MediaType;

public class GroupMember extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.lime.groupmember+json";

    public GroupMember() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * The identity of the member, in the name@domain format.
     */
    private Identity identity;

    /**
     * The role of the identity in the group.
     */
    private Group.GroupMemberRole role;

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public Group.GroupMemberRole getRole() {
        return role;
    }

    public void setRole(Group.GroupMemberRole role) {
        this.role = role;
    }
}
