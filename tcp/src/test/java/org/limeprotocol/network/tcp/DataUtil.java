package org.limeprotocol.network.tcp;

import org.limeprotocol.LimeUri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

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
    
    public static URI createUri(String scheme, Integer port) throws URISyntaxException {
        if (port == null)
        {
            port = createRandomInt(9999);
        }
        return new URI(
                String.format("%s://%s:%d",
                        scheme, createDomainName(), port));
    }
}
