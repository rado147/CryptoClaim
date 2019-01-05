package cf.cryptoclaim.whitelist;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebFilter(urlPatterns = "/*", filterName = "WhitelistFilter")
public class WhitelistFilter implements Filter {

	private WhitelistRequests whitelistRequests;
	
	public WhitelistFilter() throws IOException {
		InputStream streamData = WhitelistFilter.class.getResourceAsStream("/whitelist_requests.json");
		
		ObjectMapper objectMapper = new ObjectMapper();
		whitelistRequests = objectMapper.readValue(streamData, WhitelistRequests.class);
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		String requestMethod = httpRequest.getMethod();
		
		Set<String> parameters = new HashSet<>();
		
		Enumeration<String> parametersIter = httpRequest.getParameterNames();
		while(parametersIter.hasMoreElements()) {
			parameters.add(parametersIter.nextElement());
		}
		
		WhitelistSingleRequest pathMatchingRequest = getPathMatchingRequest(getNormalizedRequestPath(httpRequest.getRequestURI()));
		
		boolean flag = false;
		if(pathMatchingRequest == null || !pathMatchingRequest.getMethods().contains(requestMethod)) {
			flag = true;
			httpResponse.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
		} else {
			Set<String> parametersFromRequest = pathMatchingRequest.getParameters() == null ? new HashSet<>() : pathMatchingRequest.getParameters();
			for(String parameter : parameters) {
				if(!parametersFromRequest.contains(parameter)) {
					flag = true;
					httpResponse.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
				}
			}
		}
		if(!flag) {
			filterChain.doFilter(request, response);
		}
		
	}

	private WhitelistSingleRequest getPathMatchingRequest(String path) {
		Set<WhitelistSingleRequest> allowedRequests = whitelistRequests.getAllowedRequests();
		for(WhitelistSingleRequest request : allowedRequests) {
			if (path.equals(request.getPath())) {
				return request;
			}
		}
		return null;
	}
	
	private String getNormalizedRequestPath(String path) {
			return path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
	}
}
