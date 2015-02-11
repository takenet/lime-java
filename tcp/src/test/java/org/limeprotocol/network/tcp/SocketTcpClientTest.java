package org.limeprotocol.network.tcp;

import org.junit.Test;

import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;


public class SocketTcpClientTest {
    
    private ServerSocket serverSocket;
    private SocketAddress socketAddress;
    private SSLSocket sslSocket;

    private SocketTcpClient getTarget() {
        return new SocketTcpClient();
    }

    private SocketTcpClient getTargetAndConnect() throws IOException {
        SocketTcpClient target = getTarget();
        int port = Dummy.createRandomInt(9999) + 50000;
        serverSocket = new ServerSocket(port);
        socketAddress = new InetSocketAddress("localhost", port);
        target.connect(socketAddress);
        return target;
    }
    
    @Test
    public void connect_validEndpoint_connects() throws Exception {
        // Arrange
        int port = Dummy.createRandomInt(9999) + 50000;
        serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
        socketAddress = new InetSocketAddress("localhost", port);
        SocketTcpClient target = getTarget();

        // Act
        target.connect(socketAddress);
        
        // Assert
        assertTrue(serverSocket.isBound());
    }

    @Test(expected = IOException.class)
    public void connect_invalidEndpoint_throwsIOException() throws Exception {
        // Arrange
        int port = Dummy.createRandomInt(9999) + 50000;
        serverSocket = new ServerSocket(port);
        socketAddress = new InetSocketAddress("localhost", port + 1);
        SocketTcpClient target = getTarget();

        // Act
        target.connect(socketAddress);
    }

    @Test
    public void getOutputStream_connected_returnsSocketOutputStream() throws Exception {
        // Arrange
        SocketTcpClient target = getTargetAndConnect();
        
        // Act
        OutputStream actual = target.getOutputStream();
        
        // Assert
        assertNotNull(actual);
        assertEquals("java.net.SocketOutputStream", actual.getClass().getName());
    }

    @Test(expected = IOException.class)
    public void getOutputStream_notConnected_throwsIOException() throws Exception {
        // Arrange
        SocketTcpClient target = getTarget();

        // Act
        target.getOutputStream();
    }
    
    @Test
    public void getInputStream_connected_returnsSocketInputStream() throws Exception {
        // Arrange
        SocketTcpClient target = getTargetAndConnect();

        // Act
        InputStream actual = target.getInputStream();

        // Assert
        assertNotNull(actual);
        assertEquals("java.net.SocketInputStream", actual.getClass().getName());
    }


    @Test(expected = IOException.class)
    public void getInputStream_notConnected_throwsIOException() throws Exception {
        // Arrange
        SocketTcpClient target = getTarget();

        // Act
        target.getInputStream();
    }

    @Test
    public void isTlsStarted_notConnected_returnsFalse() throws Exception {
        // Arrange
        SocketTcpClient target = getTarget();
        
        // Act
        boolean actual = target.isTlsStarted();
        
        // Assert
        assertFalse(actual);
    }

    @Test
    public void isTlsStarted_connectedWithoutTls_returnsFalse() throws Exception {
        // Arrange
        SocketTcpClient target = getTargetAndConnect();

        // Act
        boolean actual = target.isTlsStarted();

        // Assert
        assertFalse(actual);
    }
}