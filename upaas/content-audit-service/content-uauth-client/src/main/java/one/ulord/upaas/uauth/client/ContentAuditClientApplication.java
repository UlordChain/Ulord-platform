/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author haibo
 * @since 5/24/18
 */
@SpringBootApplication
@ComponentScan
public class ContentAuditClientApplication {
    public static void main(String[] args){
        SpringApplication.run(ContentAuditClientApplication.class);
    }
}
