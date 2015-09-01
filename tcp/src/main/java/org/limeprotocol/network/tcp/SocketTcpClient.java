package org.limeprotocol.network.tcp;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class SocketTcpClient implements TcpClient {

    public final static int DEFAULT_SO_TIMEOUT = 5000;

    private final X509TrustManager trustManager;
    private final boolean socketTcpNoDelay;
    private final boolean socketKeepAlive;
    private final int socketSoTimeout;
    private final Socket socket;
    private SSLSocket sslSocket;
    private SSLSocketFactory sslSocketFactory;

    public SocketTcpClient() {
        this(null);
    }
    public SocketTcpClient(X509TrustManager trustManager) {
        this(trustManager, true, false, DEFAULT_SO_TIMEOUT);
    }

    public SocketTcpClient(X509TrustManager trustManager, boolean socketTcpNoDelay, boolean socketKeepAlive, int socketSoTimeout) {
        this.trustManager = trustManager;
        this.socketTcpNoDelay = socketTcpNoDelay;
        this.socketKeepAlive = socketKeepAlive;
        this.socketSoTimeout = socketSoTimeout;
        socket = new Socket();
    }

    @Override
    public void connect(SocketAddress endpoint) throws IOException {
        socket.connect(endpoint);
        socket.setTcpNoDelay(socketTcpNoDelay);
        socket.setKeepAlive(socketKeepAlive);
        socket.setSoTimeout(socketSoTimeout);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (isTlsStarted()) {
            return sslSocket.getOutputStream();
        }
        return socket.getOutputStream();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (isTlsStarted()) {
            return sslSocket.getInputStream();
        }
        return socket.getInputStream();
    }

    @Override
    public boolean isTlsStarted() {
        return sslSocket != null;
    }

    @Override
    public synchronized void startTls() throws IOException {
        if (isTlsStarted()) {
            throw new IllegalStateException("TLS is already started");
        }

        sslSocket = (SSLSocket) getSslSocketFactory().createSocket(
                socket,
                socket.getInetAddress().getHostAddress(),
                socket.getPort(),
                true);

        String[] cypherSuites = sslSocket.getSupportedCipherSuites();
        sslSocket.setEnabledCipherSuites(cypherSuites);
        
        sslSocket.startHandshake();
    }

    @Override
    public void close() throws IOException {
        if (isTlsStarted()) {
            sslSocket.close();
        }
        socket.close();
    }

    /**
     * Returns a SSL Factory instance that accepts all server certificates.
     * <pre>SSLSocket sock =
     *     (SSLSocket) getSslSocketFactory.createSocket ( host, 443 ); </pre>
     * @return  An SSL-specific socket factory.
     * http://www.howardism.org/Technical/Java/SelfSignedCerts.html
     **/
    private final SSLSocketFactory getSslSocketFactory() {
        if (sslSocketFactory == null) {
            if (trustManager != null) {
                try {
                    SSLContext sslContext = SSLContext.getInstance("SSL");
                    sslContext.init(null, new TrustManager[]{ trustManager }, null);
                    sslSocketFactory = sslContext.getSocketFactory();
                } catch (NoSuchAlgorithmException | KeyManagementException e) {
                    throw new RuntimeException("Could not set the custom TLS trust manager", e);
                }
            } else {
                sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();   
            }
        }
        return sslSocketFactory;
    }
}
