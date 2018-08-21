/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.base.config;

import one.ulord.upaas.ucwallet.service.base.intercetor.UserSecurityInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * Web config
 *
 * @author chenxin
 * @since 2018-08-10
 */
// @Configuration
public class WebAppConfig extends WebMvcConfigurerAdapter {

	/**
	 * Configuring Interceptors
	 * @param registry
	 */
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new UserSecurityInterceptor()).addPathPatterns("/api/**");
	}

	/**
	 * Early initialization load prevents inability to inject.
	 * @return
	 */
	@Bean
	public HandlerInterceptor getUserSecurityInterceptor() {
		return new UserSecurityInterceptor();
	}

}
