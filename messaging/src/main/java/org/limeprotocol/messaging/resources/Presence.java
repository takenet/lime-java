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

    private PresenceStatus status;
    private String message;
    private RoutingRule routingRule;
    private Date lastSeen;
    private int priority;
    private Boolean echo;
    private String[] instances;

    public Presence() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * The node presence status.
     */
    public PresenceStatus getStatus() {
        return status;
    }

    /**
     * The node presence status.
     */
    public void setStatus(PresenceStatus status) {
        this.status = status;
    }

    /**
     * A status message associated to the presence status.
     */
    public String getMessage() {
        return message;
    }

    /**
     * A status message associated to the presence status.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Rule to the server route envelopes addressed to the identity.
     */
    public RoutingRule getRoutingRule() {
        return routingRule;
    }

    /**
     * Rule to the server route envelopes addressed to the identity.
     */
    public void setRoutingRule(RoutingRule routingRule) {
        this.routingRule = routingRule;
    }

    /**
     * The value of the priority for the identityByPriority routing rule.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * The value of the priority for the identityByPriority routing rule.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Gets the date of the last known presence status for the node.
     * @return
     */
    public Date getLastSeen() {
        return lastSeen;
    }

    /**
     * Sets the date of the last known presence status for the node.
     * @param lastSeen
     */
    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    /**
     * Gets the echo value.
     * If true, indicates that the current session should receive the messages sent by itself.
     * @return
     */
    public Boolean getEcho() {
        return echo;
    }

    /**
     * Sets the echo value.
     * If true, indicates that the current session should receive the messages sent by itself.
     * @param echo
     */
    public void setEcho(Boolean echo) {
        this.echo = echo;
    }

    /**
     * Present instances for an identity.
     */
    public String[] getInstances() {
        return instances;
    }

    /**
     * Present instances for an identity.
     */
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
        DOMAIN
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

