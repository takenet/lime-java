package org.limeprotocol.messaging.testHelpers;

import org.limeprotocol.MediaType;
import org.limeprotocol.testHelpers.JsonConstants;

public class MessagingJsonConstants {

    public static class Capability {
        public static final String RESOURCE_CONTENT_TYPES_KEY = JsonConstants.Command.RESOURCE_KEY + "." + "contentTypes";
        public static final String RESOURCE_TYPES_KEY = JsonConstants.Command.RESOURCE_KEY + "." +"resourceTypes";
    }

    public static class Receipt {
        public static final String EVENTS_KEY = "events";
    }

    public static class ChatState {
        public static final String STATE_KEY = "state";
    }

    public static class Account {
        public static final String FULL_NAME_KEY = "fullName";
        public static final String ADDRESS_KEY = "address";
        public static final String CITY_KEY = "city";
        public static final String EMAIL_KEY = "email";
        public static final String PHONE_NUMBER_KEY = "phoneNumber";
        public static final String CELL_PHONE_NUMBER_KEY = "cellPhoneNumber";
        public static final String IS_TEMPORARY_KEY = "isTemporary";
        public static final String PASSWORD_KEY = "password";
        public static final String OLD_PASSWORD_KEY = "oldPassword";
        public static final String INBOX_SIZE_KEY = "inboxSize";
        public static final String ALLOW_ANONYMOUS_SENDER_KEY = "allowAnonymousSender";
        public static final String ALLOW_UNKNOWN_SENDER_KEY = "allowUnknownSender";
        public static final String STORE_MESSAGE_CONTENT_KEY = "storeMessageContent";
    }

    public static class Contact {
        public static final String IDENTITY_KEY = "identity";
        public static final String NAME_KEY = "name";
        public static final String IS_PENDING_KEY = "isPending";
        public static final String SHARE_PRESENCE_KEY = "sharePresence";
        public static final String SHARE_ACCOUNT_INFO_KEY = "shareAccountInfo";
    }

    public static class Delegation {
        public static final String TARGET_KEY = "target";
        public static final String DESTINATIONS_KEY = "destinations";
        public static final String COMMANDS_KEY = "commands";
        public static final String MESSAGES_KEY = "messages";
    }

    public static class Group {
        public static final String IDENTITY_KEY = "identity";
        public static final String NAME_KEY = "name";
        public static final String TYPE_KEY = "type";
        public static final String MEMBERS_KEY = "members";
    }

    public static class Presence {
        public static final String STATUS_KEY = "status";
        public static final String MESSAGE_KEY = "message";
        public static final String ROUTING_RULE_KEY = "routingRule";
        public static final String LAST_SEEN_KEY = "lastSeen";
        public static final String PRIORITY_KEY = "priority";
        public static final String INSTANCES_KEY = "instances";
    }
}
