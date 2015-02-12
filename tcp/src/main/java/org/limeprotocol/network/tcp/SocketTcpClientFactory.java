package org.limeprotocol.network.tcp;

import javax.net.ssl.X509TrustManager;

public class SocketTcpClientFactory implements TcpClientFactory {
    private final X509TrustManager trustManager;

    public SocketTcpClientFactory() {
        this(null);
    }

    public SocketTcpClientFactory(X509TrustManager trustManager) {
        this.trustManager = trustManager;
    }
    
    @Override
    public TcpClient create() {
        return new SocketTcpClient(trustManager);
    }
}
