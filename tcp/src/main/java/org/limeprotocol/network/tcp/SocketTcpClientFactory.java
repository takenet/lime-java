package org.limeprotocol.network.tcp;

public class SocketTcpClientFactory implements TcpClientFactory {
    @Override
    public TcpClient create() {
        return new SocketTcpClient();
    }
}
