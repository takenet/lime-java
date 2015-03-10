package org.limeprotocol.messaging;

import org.limeprotocol.messaging.contents.*;
import org.limeprotocol.messaging.resources.*;
import org.limeprotocol.serialization.SerializationUtil;

/**
 * Allow the registration of the package types.
 */
public class Registrator {

    /**
     * Register the documents in the package.
     */
    public static void registerDocuments() {
        SerializationUtil.registerDocumentClass(ChatState.class);
        SerializationUtil.registerDocumentClass(PlainText.class);

        SerializationUtil.registerDocumentClass(Account.class);
        SerializationUtil.registerDocumentClass(Capability.class);
        SerializationUtil.registerDocumentClass(Contact.class);
        SerializationUtil.registerDocumentClass(Delegation.class);
        SerializationUtil.registerDocumentClass(Group.class);
        SerializationUtil.registerDocumentClass(GroupMember.class);
        SerializationUtil.registerDocumentClass(Ping.class);
        SerializationUtil.registerDocumentClass(Presence.class);
        SerializationUtil.registerDocumentClass(Quota.class);
        SerializationUtil.registerDocumentClass(Receipt.class);
        SerializationUtil.registerDocumentClass(Subscription.class);
    }
}
