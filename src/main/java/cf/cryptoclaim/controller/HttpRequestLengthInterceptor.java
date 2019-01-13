package cf.cryptoclaim.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import cf.cryptoclaim.exception.TooBigRequestException;

@Component
public class HttpRequestLengthInterceptor implements HandlerInterceptor {

	private static final long MAX_REQUEST_LENGTH = 64 * 1024;
	private static final int MAX_REQUEST_URL_LENGTH = 150;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		 
		if(request.getContentLengthLong() > MAX_REQUEST_LENGTH) {
			throw new TooBigRequestException("The request is too big");
		}
		if(request.getRequestURL().length() > MAX_REQUEST_URL_LENGTH) {
			throw new TooBigRequestException("The request URL is too big");
		}
		
		return true;
	}
}
