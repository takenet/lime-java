package org.limeprotocol.messaging.resources;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.Identity;
import org.limeprotocol.MediaType;
import org.limeprotocol.Node;

public class GroupMember extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.lime.groupmember+json";

    public GroupMember() {
        super(MediaType.parse(MIME_TYPE));
    }

    private Node address;
    private GroupMemberRole role;

    /**
     * Gets the identity of the member, in the name@domain format.
     */
    public Node getAddress() {
        return address;
    }

    /**
     * Sets the identity of the member, in the name@domain format.
     */
    public void setAddress(Node address) {
        this.address = address;
    }

    /**
     * Gets the role of the identity in the group.
     */
    public GroupMemberRole getRole() {
        return role;
    }

    /**
     * Sets the role of the identity in the group.
     */
    public void setRole(GroupMemberRole role) {
        this.role = role;
    }

    public enum GroupMemberRole {
        /**
         * The member can send and receive
         * messages to the group.
         * It's the default value.
         */
        MEMBER,

        /**
         * The member can only receive messages
         * from the group, and doesn't have permission to send.
         */
        LISTENER,

        /**
         * The member can send and receive messages to
         * the group and can kick and
         * ban contacts from it.
         */
        MODERATOR,

        /**
         * The owner have the permission to manage moderators,
         * change and delete the group.
         */
        OWNER
    }
}
