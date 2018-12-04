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
		objectMapper.readValue(streamData, WhitelistRequests.class);
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
		
		WhitelistRequests.WhitelistSingleRequest pathMatchingRequest = getPathMatchingRequest(httpRequest.getRequestURI());
		
		boolean flag = false;
		if(pathMatchingRequest == null || !pathMatchingRequest.getMethods().contains(requestMethod)) {
			flag = true;
			httpResponse.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
		} else {
			Set<String> parametersFromRequest = pathMatchingRequest.getParameters();
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

	private WhitelistRequests.WhitelistSingleRequest getPathMatchingRequest(String path) {
		Set<WhitelistRequests.WhitelistSingleRequest> allowedRequests = whitelistRequests.getAllowedRequests();
		for(WhitelistRequests.WhitelistSingleRequest request : allowedRequests) {
			if (path.endsWith(request.getPath())) {
				return request;
			}
		}
		return null;
	}
	
}
