package com.tianheguoyun.uknow.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Uknow NConfigure server for SpringCloud Micro-services framework
 * @author yinhaibo
 * @since 2018/4/8
 */
@SpringBootApplication
@EnableConfigServer
public class UknowConfigServerApplication {
    public static void main(String[] args){
        SpringApplication.run(UknowConfigServerApplication.class);
    }
}
