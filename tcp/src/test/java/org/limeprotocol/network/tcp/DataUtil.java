package org.limeprotocol.network.tcp;

import org.limeprotocol.Identity;
import org.limeprotocol.LimeUri;
import org.limeprotocol.Node;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.UUID;

public class DataUtil {
    
    private static Random random = new Random();
    private static String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
    
    public static int createRandomInt(int maxValue) {
        return random.nextInt(maxValue);
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
        String randomString1 = DataUtil.createRandomString(DataUtil.createRandomInt(50));
        String randomString2 = DataUtil.createRandomString(DataUtil.createRandomInt(50));

        String text = DataUtil.createRandomString(DataUtil.createRandomInt(50));

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
}
