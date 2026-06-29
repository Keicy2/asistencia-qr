package com.asistenciaqr.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.File;

@Configuration
@ConditionalOnProperty(name = "server.ssl.enabled", havingValue = "true")
public class TomcatConfig {

    @Value("${server.ssl.key-store:keystore.p12}")
    private String keystoreFile;

    @Value("${server.ssl.key-store-password:asistenciaqr}")
    private String keystorePassword;

    @Value("${server.ssl.key-store-type:PKCS12}")
    private String keystoreType;

    @Value("${server.ssl.key-alias:asistenciaqr}")
    private String keyAlias;

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(sslConnector());
        return tomcat;
    }

    private Connector sslConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(8080);
        connector.setScheme("https");
        connector.setSecure(true);

        Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
        proto.setSSLEnabled(true);

        SSLHostConfig sslHostConfig = new SSLHostConfig();
        sslHostConfig.setSslProtocol("TLS");

        SSLHostConfigCertificate cert = new SSLHostConfigCertificate(sslHostConfig, SSLHostConfigCertificate.Type.UNDEFINED);
        cert.setCertificateKeystoreFile(new File(keystoreFile).getAbsolutePath());
        cert.setCertificateKeystorePassword(keystorePassword);
        cert.setCertificateKeystoreType(keystoreType);
        cert.setCertificateKeyAlias(keyAlias);
        sslHostConfig.addCertificate(cert);

        proto.addSslHostConfig(sslHostConfig);

        return connector;
    }
}
