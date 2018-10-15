/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Web服务入口类，提供Web容器初始化
 * 
 * @author chenxin
 * @since 2018-08-10
 */
//@SpringBootApplication
//@EnableAsync
////@ImportResource("classpath:application-service.xml")
//public class Application {
//
//	public static void main(String[] args){
//		SpringApplication.run(Application.class, args);
//	}
//
//	@Bean
//	public CharacterEncodingFilter initializeCharacterEncodingFilter() {
//		CharacterEncodingFilter filter = new CharacterEncodingFilter();
//		filter.setEncoding("utf-8");
//		filter.setForceEncoding(true);
//		return filter;
//	}
//
//}

@SpringBootApplication
@EnableAsync
public class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

}


