/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service;

import one.ulord.upaas.ucwallet.service.base.common.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.filter.CharacterEncodingFilter;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

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
public class Application extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CharacterEncodingFilter initializeCharacterEncodingFilter() {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding(Constants.CHARACTER_ENCODING);
		filter.setForceEncoding(true);
		return filter;
	}

}


