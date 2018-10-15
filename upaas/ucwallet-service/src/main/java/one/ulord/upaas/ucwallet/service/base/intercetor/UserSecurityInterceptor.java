/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.base.intercetor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import one.ulord.upaas.ucwallet.service.base.common.Constants;
import one.ulord.upaas.ucwallet.service.base.common.RedisUtil;
//import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Providing user security verification interception processing
 *
 * @author chenxin
 * @since 10/8/18
 */
//@Component
//@Aspect
public class UserSecurityInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private RedisUtil redisUtil;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		response.setCharacterEncoding(Constants.CHARACTER_ENCODING);
		response.setContentType(Constants.CONTENT_TYPE);

//		redisUtil.set("ulord","100000000000000000");
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler, ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) throws Exception {
	}

}
