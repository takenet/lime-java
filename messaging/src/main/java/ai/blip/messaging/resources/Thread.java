package ai.blip.messaging.resources;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.Identity;
import org.limeprotocol.MediaType;

public class Thread extends DocumentBase {
    public static final String MIME_TYPE = "application/vnd.iris.thread+json";

    /**
     * Initializes a new instance of the @Thread class.
     */
    public Thread() {
        super(MediaType.parse(MIME_TYPE));
    }

    private Identity ownerIdentity;
    private Identity identity;
    private ThreadMessage lastMessage;
    private long unreadMessages;
    private Identity serviceIdentity;

    /**
     * Gets the thread owner identity.
     *
     * @return
     */
    public Identity getOwnerIdentity() {
        return ownerIdentity;
    }

    /**
     * Sets the thread owner identity
     *
     * @param ownerIdentity
     */
    public void setOwnerIdentity(Identity ownerIdentity) {
        this.ownerIdentity = ownerIdentity;
    }

    /**
     * Gets the thread remote identity.
     *
     * @return
     */
    public Identity getIdentity() {
        return identity;
    }

    /**
     * Sets the thread remote identity
     *
     * @param identity
     */
    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    /**
     * Gets the thread last message.
     *
     * @return
     */
    public ThreadMessage getLastMessage() {
        return lastMessage;
    }

    /**
     * Sets the thread last message
     *
     * @param lastMessage
     */
    public void setLastMessage(ThreadMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    /**
     * Gets the number of unread messages.
     * Unread messages are messages withoud consumed notification.
     *
     * @return
     */
    public long getUnreadMessages() {
        return unreadMessages;
    }

    /**
     * Sets the number of unread messages.
     * Unread messages are messages withoud consumed notification.
     *
     * @param unreadMessages
     */
    public void setUnreadMessages(long unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public Identity getServiceIdentity() {
        return serviceIdentity;
    }

    public void setServiceIdentity(Identity serviceIdentity) {
        this.serviceIdentity = serviceIdentity;
    }
}