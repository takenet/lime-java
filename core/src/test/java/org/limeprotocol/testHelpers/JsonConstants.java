package org.limeprotocol.testHelpers;

public class JsonConstants {
    public static class Envelope {
        public static final String ID_KEY = "id";
        public static final String FROM_KEY = "from";
        public static final String PP_KEY = "pp";
        public static final String TO_KEY = "to";
        public static final String METADATA_KEY = "metadata";

        public static final String[] ALL_KEYS = new String[] { ID_KEY, FROM_KEY, PP_KEY, TO_KEY, METADATA_KEY };

        public static String getMetadataKeyFromRoot(String metadataKey) {
            return METADATA_KEY + "." + metadataKey;
        }

    }

    public static class Session {
        public static final String STATE_KEY = "state";
        public static final String MODE_KEY = "mode";
        public static final String ENCRYPTION_OPTIONS_KEY = "encryptionOptions";
        public static final String ENCRYPTION_KEY = "encryption";
        public static final String COMPRESSION_OPTIONS_KEY = "compressionOptions";
        public static final String COMPRESSION_KEY = "compression";
        public static final String SCHEME_OPTIONS_KEY = "schemeOptions";
        public static final String SCHEME_KEY = "scheme";
        public static final String AUTHENTICATION_KEY = "authentication";
        public static final String REASON_KEY = "reason";
    }

    public static class Reason {
        public static final String CODE_KEY = "code";
        public static final String DESCRIPTION_KEY = "description";

        public static final String CODE_KEY_FROM_ROOT = Session.REASON_KEY + "." + CODE_KEY;
        public static final String DESCRIPTION_KEY_FROM_ROOT = Session.REASON_KEY + "." + DESCRIPTION_KEY;

    }

    public static class PlainAuthentication {
        public static final String PASSWORD_KEY = "password";
        public static final String PASSWORK_KEY_FROM_ROOT = Session.AUTHENTICATION_KEY + "." + PASSWORD_KEY;
    }

    public static class Command {
        public static final String URI_KEY = "uri";
        public static final String TYPE_KEY = Message.TYPE_KEY;
        public static final String RESOURCE_KEY = "resource";
        public static final String METHOD_KEY = "method";
        public static final String STATUS_KEY = "status";
        public static final String REASON_KEY = "reason";
    }

    public static class Message {
        public static final String TYPE_KEY = "type";
        public static final String CONTENT_KEY = "content";
        public static final String VALUE_KEY = "value";
        public static final String CONTENT_VALUE_KEY = CONTENT_KEY + "." + VALUE_KEY;
    }

    public static class Notification {
        public static final String EVENT_KEY = "event";
        public static final String REASON_KEY = "reason";
        public static final String CODE_FROM_REASON_KEY = REASON_KEY + "." + Reason.CODE_KEY;
        public static final String DESCRIPTION_FROM_REASON_KEY = REASON_KEY + "." + Reason.DESCRIPTION_KEY;
    }

    public static class PlainText {
        public static final String CONTENT_KEY = "content";
        public static final String TEXT_KEY = "text";
        public static final String CONTENT_TEXT_KEY = CONTENT_KEY + "." + TEXT_KEY;
    }

    public static class DocumentCollection {
        public static final String ITEMS_KEY = "items";
        public static final String ITEM_TYPE_KEY = "itemType";
        public static final String TOTAL_KEY = "total";

    }
}
