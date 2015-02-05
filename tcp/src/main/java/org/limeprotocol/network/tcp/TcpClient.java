package org.limeprotocol.network.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Abstraction over the Java Socket class,
 * to allow better testing.
 */
public interface TcpClient {
    void connect(SocketAddress endpoint) throws IOException;

    OutputStream getOutputStream() throws IOException;

    InputStream getInputStream() throws IOException;
    
    boolean isTlsStarted();
    
    void startTls() throws IOException;

    void close() throws IOException;
}
