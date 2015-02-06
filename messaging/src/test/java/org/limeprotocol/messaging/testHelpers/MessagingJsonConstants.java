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

}
