package org.limeprotocol.messaging.resources;

import org.limeprotocol.*;

import java.net.URI;
import java.util.Date;

public class Group extends DocumentBase {

    public static final String MIME_TYPE = "application/vnd.lime.group+json";

    public Group() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * IDENTITY of the group, in the group-id@groups.domain.com format.
     */
    private Identity identity;

    /**
     * Name of the group.
     */
    private String name;

    /**
     * Type of the group.
     */
    private GroupType type;

    /**
     * The group photo URI.
     */
    private URI photoUri;

    /**
     * IDENTITY of the group's creator.
     */
    private Identity creator;

    /**
     * Creation date of the group.
     */
    private Date created;

    /**
     * Members uri of the contact group.
     */
    private LimeUri membersUri;

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

    public URI getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(URI photoUri) {
        this.photoUri = photoUri;
    }

    public Identity getCreator() {
        return creator;
    }

    public void setCreator(Identity creator) {
        this.creator = creator;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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
}