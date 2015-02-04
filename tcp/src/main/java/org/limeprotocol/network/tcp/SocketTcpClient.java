package org.limeprotocol.network.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketTcpClient implements TcpClient {
    
    private Socket socket;
    
    public SocketTcpClient() {
        socket = new Socket();
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void connect(SocketAddress endpoint) throws IOException {
        socket.connect(endpoint);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
