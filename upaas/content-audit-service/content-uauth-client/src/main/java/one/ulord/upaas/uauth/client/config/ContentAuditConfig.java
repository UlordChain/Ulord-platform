/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.client.config;

import one.ulord.upaas.common.communication.client.UPaaSCommandClient;
import one.ulord.upaas.uauth.client.communication.ContentAuditClientHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author haibo
 * @since 5/24/18
 */
@Configuration
public class ContentAuditConfig {
    @Value("${upaas.uauth.server.host:localhost}")
    private String serverHost;

    @Value("${upaas.uauth.server.port:8070}")
    private int serverPort;

    @Value("${upaas.uauth.client.keepLiveInterval:30}")
    private long keepLiveInterval;

    @Autowired
    ContentAuditClientHandler handler;

    @Bean
    public UPaaSCommandClient uPaaSCommandClient(){
        UPaaSCommandClient client = new UPaaSCommandClient(serverHost, serverPort, handler, keepLiveInterval);
        return client;
    }
}
