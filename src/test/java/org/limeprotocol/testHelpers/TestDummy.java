package org.limeprotocol.testHelpers;

import org.limeprotocol.Identity;
import org.limeprotocol.Node;
import org.limeprotocol.Reason;
import org.limeprotocol.Session;
import org.limeprotocol.Session.*;
import org.limeprotocol.security.PlainAuthentication;
import org.limeprotocol.util.StringUtils;

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

    public static PlainAuthentication createPlainAuthentication()
    {
        PlainAuthentication authentication = new PlainAuthentication();
        authentication.setToBase64Password(createRandomString(8));
        return authentication;
    }

    public static Reason createReason() {
        return new Reason(createRandomInt(100), createRandomString(100));
    }
}
