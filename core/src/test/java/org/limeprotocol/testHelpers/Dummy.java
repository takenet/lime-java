package org.limeprotocol.testHelpers;

import org.limeprotocol.*;
import org.limeprotocol.Session.SessionState;
import org.limeprotocol.security.PlainAuthentication;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Dummy {

    private static Random random = new Random();
    private static String chars = "abcdefghijklmnopqrstuvwxyz0123456789";

    public static int createRandomInt(int maxValue) {
        return random.nextInt(maxValue);
    }

    public static long createRandomLong() {
        return random.nextLong();
    }
    
    public static String createRandomString(int size) {
        StringBuilder sb = new StringBuilder(size);
        for( int i = 0; i < size; i++ )
            sb.append(chars.charAt(random.nextInt(chars.length())));
        return sb.toString();
    }

    public static String createDomainName() {
        return String.format("%s.com", createRandomString(10));
    }

    public static String createSubdomainName()
    {
        return createRandomString(10);
    }

    public static String createInstanceName()
    {
        return createRandomString(5);
    }

    public static Identity createIdentity()
    {
        return new Identity(createRandomString(8),createDomainName());
    }

    public static Node createNode() {
        Identity identity = createIdentity();
        return new Node(identity.getName(), identity.getDomain(), createInstanceName());
    }

    public static URI createUri() throws URISyntaxException {
        return createUri("net.tcp", 55321);
    }

    public static URI createUri(String scheme, Integer port) throws URISyntaxException {
        if (port == null)
        {
            port = createRandomInt(9999);
        }
        return new URI(
                String.format("%s://%s:%d",
                        scheme, createDomainName(), port));
    }

    public static String createMessageJson()
    {
        UUID id = UUID.randomUUID();
        Node from = createNode();
        Node pp = createNode();
        Node to = createNode();

        String randomKey1 = "randomString1";
        String randomKey2 = "randomString2";
        String randomString1 = createRandomString(createRandomInt(50));
        String randomString2 = createRandomString(createRandomInt(50));

        String text = createRandomString(createRandomInt(50));

        return String.format(
                "{{\"type\":\"text/plain\",\"content\":\"%s\",\"id\":\"%s\",\"from\":\"%s\",\"pp\":\"%s\",\"to\":\"%s\",\"metadata\":{{\"%s\":\"%s\",\"%s\":\"%s\"}}}}",
                text,
                id,
                from,
                pp,
                to,
                randomKey1,
                randomString1,
                randomKey2,
                randomString2
        );
    }

    public static Map<String, String> createRandomMetadata(String... keys) {
        keys = keys == null || keys.length == 0 ?
                new String[] { "randomString1", "randomString2" } :
                keys;

        HashMap<String, String> map = new HashMap<>();
        for (String key : keys) {
            map.put(key, createRandomString(50));
        }
        return map;
    }

    public static Session createSession() {
        return createSession(SessionState.NEW);
    }

    public static Session createSession(SessionState state) {
        Session session = new Session();
        session.setId(EnvelopeId.newId());
        session.setFrom(createNode());
        session.setTo(createNode());
        session.setState(state);

        return session;
    }

    public static JsonDocument createJsonDocument()
    {
        HashMap<String, Object> documentNodes = new HashMap<>();
        documentNodes.put(createRandomString(10), createRandomString(50));
        documentNodes.put(createRandomString(10), createRandomInt(50));

        JsonDocument jsonDocument = new JsonDocument(documentNodes, createJsonMediaType());
        return jsonDocument;
    }

    public static PlainDocument createPlainDocument()
    {
        return new PlainDocument(
                createRandomString(50),
                createPlainMediaType());
    }

    public static MediaType createPlainMediaType()
    {
        return new MediaType(
                createRandomString(10),
                createRandomString(10),
                null
        );

    }

    public static MediaType createJsonMediaType(){

        return new MediaType(
                "application",
                createRandomString(10),
                "json"
        );
    }


    public static Message createMessage(Document content)
    {
        Message message = new Message(EnvelopeId.newId());

        message.setContent(content);
        message.setFrom(createNode());
        message.setTo(createNode());

        return message;
    }

    public static PlainDocument createTextContent() {
        return new PlainDocument(createRandomString(150), MediaType.parse("text/plain"));
    }

    public static Command createCommand(){
        return createCommand(null);
    }

    public static Command createCommand(Document resource) {
        Command command = new Command(EnvelopeId.newId());
        command.setFrom(Dummy.createNode());
        command.setTo(Dummy.createNode());
        command.setMethod(Command.CommandMethod.GET);
        command.setResource(resource);

        return command;
    }

    public static PlainAuthentication createPlainAuthentication()
    {
        PlainAuthentication authentication = new PlainAuthentication();
        authentication.setToBase64Password(createRandomString(8));
        return authentication;
    }

    public static Reason createReason() {
        return new Reason(createRandomInt(100), createRandomString(100));
    }

    public static LimeUri createAbsoluteLimeUri(){
        return new LimeUri(LimeUri.LIME_URI_SCHEME + "://" + createIdentity() +
                "/" + createRandomString(10));
    }

    public static LimeUri createRelativeLimeUri(){
        return new LimeUri("/" + createRandomString(10));
    }

    public static Notification createNotification(Notification.Event event) {
        Notification notification = new Notification();
        notification.setFrom(createNode());
        notification.setTo(createNode());
        notification.setEvent(event);
        return notification;
    }

    public static DocumentCollection createDocumentCollection(Document ... documents){
        DocumentCollection documentCollection = new DocumentCollection();
        documentCollection.setItemType(documents[0].getMediaType());
        documentCollection.setTotal(documents.length);
        documentCollection.setItems(documents);
        return documentCollection;
    }
}
