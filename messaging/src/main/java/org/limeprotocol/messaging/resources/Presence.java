package org.limeprotocol.messaging.resources;

import org.limeprotocol.Document;
import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

import java.util.Date;

/**
 * Represents the availability status of a node in a network.
 * A node can only receive envelopes from another nodes in the network
 * if it sets its presence to an available status (except from the server,
 * who always knows if a node is available or node, since this information
 * is enforced by the existing session).
 * In a new session, the node starts with an unavailable status.
 */
public class Presence extends DocumentBase {
    public static final String MIME_TYPE = "application/vnd.lime.presence+json";

    public Presence() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
    * The node presence status.
    */
    private PresenceStatus status;

    /**
    * A status message associated
    * to the presence status.
    */
    private String message;

    /**
    * Rule to the server route envelopes
    * addressed to the identity.
    */
    private RoutingRule routingRule;


    private Date lastSeen;

    /**
    * The value of the priority for
    * the identityByPriority routing rule.
    */
    private int priority;

    /**
    * Present instances for
    * a identity.
    */
    private String[] instances;

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public PresenceStatus getStatus() {
        return status;
    }

    public void setStatus(PresenceStatus status) {
        this.status = status;
    }

    public RoutingRule getRoutingRule() {
        return routingRule;
    }

    public void setRoutingRule(RoutingRule routingRule) {
        this.routingRule = routingRule;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getInstances() {
        return instances;
    }

    public void setInstances(String[] instances) {
        this.instances = instances;
    }

    public enum RoutingRule {
        /**
         * Only deliver envelopes addressed
         * to the current session instance (name@domain/instance).
         */
        INSTANCE,

        /**
         * Deliver envelopes addressed to the current session instance
         * (name@domain/instance) and envelopes addressed to the
         * identity (name@domain)
         */
        IDENTITY,

        /**
         * Deliver envelopes addressed to the current session
         * instance (name@domain/instance) and envelopes addressed
         * to the identity (name@domain) if the distance from the
         * origitator is the smallest among the available
         * nodes of the identity with this setting.
         */
        IDENTITY_BY_DISTANCE,

        /**
         * Deliver any envelopes addressed to the identity name@domain,
         * including the envelopes addressed to any specific instance.
         */
        PROMISCUOUS,

        /**
         * Deliver envelopes addressed to the current session
         * instance (name@domain/instance) and envelopes addresses to the node domain.
         * This rule is intended to be used only for external domain authorities
         * (gateways) and sub-domain authorities (applications).
         */
        DOMAIN,

        /**
         * Deliver envelopes addressed to the current session
         * instance (name@domain/instance) and envelopes addresses to the node domain
         * if the distance from the originator is the smallest among the available
         * nodes of the domain with this setting.
         * This rule is intended to be used only for external domain authorities
         * (gateways) and sub-domain authorities (applications)
         */
        DOMAIN_BY_DISTANCE
    }

    /**
     * Possible presence status values
     */
    public enum PresenceStatus {

        /**
         * The node is not available for messaging and
         * SHOULD not receive any envelope by any node,
         * except by the connected server.
         */
        UNAVAILABLE,

        /**
         * The node is available for messaging
         * and envelopes can be routed to the node
         * according to the defined routing rule.
         */

        AVAILABLE,

        /**
         * The node is available but the senders should notice
         * that it is busy and doesn't want to the disturbed
         * or it is on heavy load and don't want to receive
         * any envelope.
         */
        BUSY,

        /**
         * The node is available but the senders should notice
         * that it may not be reading or processing
         * the received envelopes.
         */
        AWAY,

        /**
         * The node is available for messaging but the actual
         * stored presence value is unavailable.
         */
        INVISIBLE
    }

}

