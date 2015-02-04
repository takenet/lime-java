package org.limeprotocol.network.tcp;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class TcpTransportTest {

    public void open_notConnectedValidUri_connectsClientAndCallsGetStream() {
        
        
    }
    
    @Test
    public void open_validUri_connectToRemoteNode() throws URISyntaxException, IOException {
        TcpTransport tcpTransport = new TcpTransport(null);
        URI serverUri = new URI("net.tcp://takenet-iris.cloudapp.net:55321");
        tcpTransport.open(serverUri);
    }

    @Test(expected = IOException.class)
    public void open_invalidUriHostName_throwsIOException() throws URISyntaxException, IOException {
        TcpTransport tcpTransport = new TcpTransport(null);
        URI serverUri = new URI("net.tcp://invalidhostname.local:55321");
        tcpTransport.open(serverUri);
    }
}