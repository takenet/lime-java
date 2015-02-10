package org.limeprotocol.network.tcp;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CustomTrustManager implements X509TrustManager {

    private final X509Certificate[] acceptedIssuers;

    public CustomTrustManager(X509Certificate[] acceptedIssuers) {
        this.acceptedIssuers = acceptedIssuers;
    }
    
    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return acceptedIssuers;
    }
}
