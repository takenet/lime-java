package org.limeprotocol.network.tcp;

import org.junit.Test;

import static org.junit.Assert.*;

public class TcpTransportTest {

    @Test
    public void test_TcpTransport(){
        TcpTransport tcpTransport = new TcpTransport(null);
        assertNotNull(tcpTransport);
    }

}
