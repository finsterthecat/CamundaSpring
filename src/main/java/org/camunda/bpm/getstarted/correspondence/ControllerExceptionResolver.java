package org.camunda.bpm.getstarted.correspondence;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * Handles all Controller Exceptions. Spring uses any bean that implements HandlerExceptionResolver to handle controller exceptions.
 * Order determines the order in which the beans implementing the interface are executed. Bigger means executed first.
 * 
 * @see < a href="https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc">Exception Handling in Spring MVC</a>
 *
 */
@Named
public class ControllerExceptionResolver extends SimpleMappingExceptionResolver
		implements HandlerExceptionResolver, Ordered {

	public ControllerExceptionResolver() {
		setWarnLogCategory(ControllerExceptionResolver.class.getName());
	}

	@Override
	public String buildLogMessage(Exception e, HttpServletRequest req) {
		return "Controller Exception: " + e.getLocalizedMessage();
	}

	@Override
	public ModelAndView doResolveException(HttpServletRequest req, HttpServletResponse resp, Object handler,
			Exception ex) {
		ModelAndView mav = super.doResolveException(req, resp, handler, ex);
		if (mav != null) {
			mav.addObject("url", req.getRequestURL());
		}
		return mav;
	}

	@Override
	public int getOrder() {
		return Integer.MAX_VALUE-1;
	}

}
