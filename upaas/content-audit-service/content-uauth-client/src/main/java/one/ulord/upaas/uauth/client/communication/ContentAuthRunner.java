/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.client.communication;

import one.ulord.upaas.common.communication.client.UPaaSCommandClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author haibo
 * @since 5/24/18
 */
@Component
public class ContentAuthRunner implements CommandLineRunner {


    @Autowired
    UPaaSCommandClient uPaaSCommandClient;


    @Override
    public void run(String... args) throws Exception {
        // run tcp server
        uPaaSCommandClient.run();
    }
}
