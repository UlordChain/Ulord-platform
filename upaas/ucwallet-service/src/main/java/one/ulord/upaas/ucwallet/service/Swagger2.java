/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service;

import io.swagger.annotations.ApiOperation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.test.context.ContextConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger config init
 * 
 * @author chenxin
 * @since 2018-08-10
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

//	@Bean
//	public Docket buildDocket() {
//		return new Docket(DocumentationType.SWAGGER_2)
//				.apiInfo(buildApiInfo())
//				.select()
//				//要扫描的API(Controller)基础包
//				.apis(RequestHandlerSelectors.basePackage("one.ulord.upaas.ucwallet.service.controller"))
//				.paths(PathSelectors.any())
//				.build();
//	}

	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(buildApiInfo()).select()
				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)).build();
	}

	private ApiInfo buildApiInfo() {
		return new ApiInfoBuilder().title("RESTful API for UCWallet-Service")
				.description("Provide reference to DAPP caller.").termsOfServiceUrl("http://ulord.one")
				.contact("chenxin").version("1.0").build();
	}

}
