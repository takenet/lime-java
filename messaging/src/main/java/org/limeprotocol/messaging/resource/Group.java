package org.limeprotocol.messaging.resource;

import org.limeprotocol.Document;
import org.limeprotocol.Identity;
import org.limeprotocol.LimeUri;
import org.limeprotocol.MediaType;

public class Group implements Document {

    public final String MIME_TYPE = "application/vnd.lime.group+json";

    public final String IDENTITY_KEY = "identity";
    public final String NAME_KEY = "name";
    public final String TYPE_KEY = "type";
    public final String MEMBERS_KEY = "members";
    private final MediaType mediaType;


    public Group() {
        this.mediaType = MediaType.parse(MIME_TYPE);
    }

    /**
     * IDENTITY of the group, in the group-id@groups.domain.com format.
     */
    public Identity identity;

    /**
     * Name of the group.
     */
    public String name;

    /**
     * Type of the group.
     */
    public GroupType type;

    /**
     * Members uri of the contact group.
     */
    public LimeUri membersUri;

    @Override
    public MediaType getMediaType() {
        return this.mediaType;
    }

    public GroupType getType() {
        return type;
    }

    public Identity getIdentity() {
        return identity;
    }

    public LimeUri getMembersUri() {
        return membersUri;
    }

    public String getName() {
        return name;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMembersUri(LimeUri membersUri) {
        this.membersUri = membersUri;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public enum GroupType {
        /**
         * The group exists while the owner session that created it is active.
         * This type of group is useful for mass message sending, since the groups
         * application is optimized to send large amounts of messages.
         * The temporary groups are private.
         */
        TEMPORARY,

        /**
         * The group is not discoverable and someone
         * can join only if is invited by owner or a moderator.
         */
        PRIVATE,

        /**
         * Any authenticated node in the domain can join the group.
         */
        PUBLIC
    }

    public class GroupMember implements Document {

        private final MediaType mediaType;
        public final String MIME_TYPE = "application/vnd.lime.groupmember+json";


        public GroupMember() {
            this.mediaType = MediaType.parse(MIME_TYPE);
        }

        /**
         * The identity of the member, in the name@domain format.
         */
        private Identity identity;

        /**
         * The role of the identity in the group.
         */
        public GroupMemberRole role;

        @Override
        public MediaType getMediaType() {
            return null;
        }

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