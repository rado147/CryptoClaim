package cf.cryptoclaim.auth;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import cf.cryptoclaim.crypto.ClaimEncryptionService;
import cf.cryptoclaim.exception.AuthenticationFailedException;
import cf.cryptoclaim.exception.CryptoClaimException;
import cf.cryptoclaim.exception.WrongPasswordException;

@WebFilter(urlPatterns = "/*", filterName = "authenticationFilter")
public class CryptoClaimAuthenticationFilter implements Filter {

	private static final String CLIENT_ID_KEY = "client_id";
	
	
	private static final Set<String> NO_AUTHENTICATION_URI_POSTFIXES = new HashSet<>(Arrays.asList("/register", "/"));
	
	@Autowired
	private JWTService jwtService;
	
	@Autowired
	private ClaimEncryptionService claimEncryptionService;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	    SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
	            filterConfig.getServletContext());
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		
		if(isAuthenticationRequired(httpServletRequest, httpServletResponse, chain)) {
			return;
		}
		
		String authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
		
		String[] authorizationHeaderParts = authorizationHeader == null ? null : authorizationHeader.split(" ");
		String clientId = request.getParameter(CLIENT_ID_KEY);
		if(clientId != null && authorizationHeaderParts != null && authorizationHeaderParts[0].equals("Bearer")) {
			try {
				jwtService.verifyJWT(httpServletRequest, authorizationHeaderParts[1], clientId, claimEncryptionService.getPublicKey(clientId));
			} catch (CryptoClaimException | AuthenticationFailedException | WrongPasswordException e) {
				httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
				return;
			}
			chain.doFilter(httpServletRequest, httpServletResponse);
			return;
		}
		httpServletResponse.sendError(HttpStatus.BAD_REQUEST.value());
	}

	private boolean isAuthenticationRequired(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		String requestUri = request.getRequestURI();
		
		for(String postfix : NO_AUTHENTICATION_URI_POSTFIXES) {
			if(requestUri.equals(postfix)) {
				chain.doFilter(request, response);
				return true;
			}
		}
		return false;
	}
}
