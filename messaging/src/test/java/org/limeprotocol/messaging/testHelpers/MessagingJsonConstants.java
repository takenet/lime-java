package org.limeprotocol.messaging.testHelpers;

import org.limeprotocol.testHelpers.JsonConstants;

public class MessagingJsonConstants {

    public static class Capability {
        public static final String RESOURCE_CONTENT_TYPES_KEY = JsonConstants.Command.RESOURCE_KEY + "." + "contentTypes";
        public static final String RESOURCE_TYPES_KEY = JsonConstants.Command.RESOURCE_KEY + "." +"resourceTypes";
    }

    public static class Receipt {
        public static final String EVENTS_KEY = "events";
    }

    public static class Contact{
        public static final String IDENTITY_KEY = "identity";
        public static final String NAME_KEY = "name";
        public static final String IS_PENDING_KEY = "isPending";
        public static final String SHARE_PRESENCE_KEY = "sharePresence";
        public static final String SHARE_ACCOUNT_INFO_KEY = "shareAccountInfo";
        public static final String PRIORITY_KEY = "priority";
    }

}
