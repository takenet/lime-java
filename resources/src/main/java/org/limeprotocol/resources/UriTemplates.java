package org.limeprotocol.resources;

/**
 * Stores the Uri templates
 * for the common protocol resources
 */
public class UriTemplates
{
    /**
     * Template for the
     * account resource
     */
    public final static String ACCOUNT = "/account";

    /**
     * Template for the
     * presence resource
     */
    public final static String PRESENCE = "/presence";

    /**
     * Template for the
     * contacts resource
     */
    public final static String CONTACTS = "/contacts";

    /**
     * Template for a
     * specific contact resource
     */
    public final static String CONTACT = "/contacts/{contactIdentity}";

    /**
     * Template for the
     * groups resource
     */
    public final static String GROUPS = "/groups";

    /**
     * Template for a
     * specific group resource
     */
    public final static String GROUP = "/groups/{groupIdentity}";

    /**
     * Template for a
     * specific group members
     * resource
     */
    public final static String GROUP_MEMBERS = "/groups/{groupIdentity}/members";

    /**
     * Template for a
     * specific group member
     * resource
     */
    public final static String GROUP_MEMBER = "/groups/{groupIdentity}/members/{memberIdentity}";

    /**
     * Template for the
     * ping resource
     */
    public final static String PING = "/ping";

    /**
     * Template for the
     * receipt resource
     */
    public final static String RECEIPT = "/receipt";
}
