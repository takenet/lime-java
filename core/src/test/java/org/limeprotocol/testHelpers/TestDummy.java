package org.limeprotocol.testHelpers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.sun.istack.internal.NotNull;
import org.limeprotocol.*;
import org.limeprotocol.Session.*;
import org.limeprotocol.messaging.contents.PlainText;
import org.limeprotocol.security.PlainAuthentication;
import org.limeprotocol.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TestDummy {

    public static String createInstanceName() {
        return null;
    }

    public static String createRandomString(int size) {
        String random = "";
        while(random.length() < size) {
            random += UUID.randomUUID().toString().replace("-", "").toLowerCase();
        }

        return random.substring(size);
    }

    public static int createRandomInt(int size) {
        return new Random().nextInt(size);
    }

    public static String createDomainName() {
        return StringUtils.format("{0}.com", createRandomString(10));
    }

    public static Identity createIdentity() {
        return new Identity(createRandomString(8), createDomainName());
    }

    public static Node createNode() {
        Identity identity = createIdentity();

        Node node = new Node(identity);
        node.setInstance(createInstanceName());

        return node;
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
        return createSession(SessionState.New);
    }

    public static Session createSession(SessionState state) {
        Session session = new Session();
        session.setId(UUID.randomUUID());
        session.setFrom(createNode());
        session.setTo(createNode());
        session.setState(state);

        return session;
    }

    public static PlainText createTextContent()
    {
        return new PlainText(createRandomString(150));
    }

    public static JsonDocument createJsonDocument()
    {
        HashMap<String, Object> documentNodes = new HashMap<String, Object>();
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
        Message message = new Message();

        message.setContent(content);
        message.setFrom(createNode());
        message.setTo(createNode());

        return message;
    }

    public static Command createCommand(){
        Command command = new Command();
        command.setFrom(TestDummy.createNode());
        command.setTo(TestDummy.createNode());
        command.setMethod(Command.CommandMethod.Get);
        command.setStatus(Command.CommandStatus.Pending);
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
        return new LimeUri(LimeUri.LIME_URI_SCHEME + ";//" + createIdentity() +
                "/" + createRandomString(10));
    }

    public static URI createUri(){
        try {
            return new URI("http://" + createDomainName() + ":" + createRandomInt(9999));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);

        }
    }
}
