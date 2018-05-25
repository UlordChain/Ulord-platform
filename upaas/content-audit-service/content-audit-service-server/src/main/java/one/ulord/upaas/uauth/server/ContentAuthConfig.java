/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.server;

import one.ulord.upaas.common.communication.server.UPaaSCommandServer;
import one.ulord.upaas.common.communication.server.UPaaSServerManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author haibo
 * @since 5/23/18
 */
@Configuration
public class ContentAuthConfig {
    @Value("${upaas.uauth.server.port:8070}")
    private int serverPort;

    @Bean
    public UPaaSCommandServer uPaaSCommandServer() throws Exception {
        UPaaSCommandServer server = new UPaaSCommandServer(serverPort);
        return server;
    }

    @Bean
    public UPaaSServerManager uPaaSServerManager(UPaaSCommandServer uPaaSCommandServer){
        return uPaaSCommandServer.getServerManager();
    }
}
