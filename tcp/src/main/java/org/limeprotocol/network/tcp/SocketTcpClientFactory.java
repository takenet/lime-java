package org.limeprotocol.network.tcp;

import javax.net.ssl.X509TrustManager;

public class SocketTcpClientFactory implements TcpClientFactory {
    private final X509TrustManager trustManager;
    private final boolean socketTcpNoDelay;
    private final boolean socketKeepAlive;
    private final int socketSoTimeout;

    public SocketTcpClientFactory() {
        this(null);
    }

    public SocketTcpClientFactory(X509TrustManager trustManager) {
        this(trustManager, true, false, SocketTcpClient.DEFAULT_SO_TIMEOUT);
    }

    public SocketTcpClientFactory(X509TrustManager trustManager, boolean socketTcpNoDelay, boolean socketKeepAlive, int socketSoTimeout) {
        this.trustManager = trustManager;
        this.socketTcpNoDelay = socketTcpNoDelay;
        this.socketKeepAlive = socketKeepAlive;
        this.socketSoTimeout = socketSoTimeout;
    }

    @Override
    public TcpClient create() {
        return new SocketTcpClient(trustManager, socketTcpNoDelay, socketKeepAlive, socketSoTimeout);
    }
}
