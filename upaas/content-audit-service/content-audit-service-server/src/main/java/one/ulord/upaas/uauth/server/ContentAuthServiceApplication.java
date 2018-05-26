/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author haibo
 * @since 5/16/18
 */
@SpringBootApplication
@ComponentScan
public class ContentAuthServiceApplication {
    public static void main(String[] args){
        SpringApplication.run(ContentAuthServiceApplication.class);
    }

}
