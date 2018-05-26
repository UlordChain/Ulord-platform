/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.server.communication;

import one.ulord.upaas.common.communication.UPaaSCommandCode;
import one.ulord.upaas.common.communication.server.UPaaSCommandServer;
import one.ulord.upaas.common.communication.server.UPaaSServerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Content Auth Runner
 * @author haibo
 * @since 5/16/18
 */
@Component
public class ContentAuthRunner implements CommandLineRunner {


    @Autowired
    UPaaSCommandServer uPaaSCommandServer;

    @Autowired
    CAuthServerCommandHandler serverCommandHandler;


    @Override
    public void run(String... args) throws Exception {
        // register command processer
        uPaaSCommandServer.getServerManager().registerCommandHandler(
                String.valueOf(UPaaSCommandCode.BASE_TYPE),
                serverCommandHandler);

        // run tcp server
        uPaaSCommandServer.run();
    }
}
