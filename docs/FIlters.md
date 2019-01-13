# Servlet Filters and Request Interceptors used in CryptoClaim

### Authentication filter

This filtered is used for performing authentication with Signed JSON Web Tokens (if authentication is required):
	- The JWT token should be put in the 'Authorization' header of the request and it is a bearer token
	- In the doFilter(ServletRequest request, ServletResponse response, FilterChain chain) method of the filter the token will be verified by the JWT service:
		- All claims will be verified: time of issue, audience etc. (including the custom 'psw' (password) claim)
	- In case of missing Authorization header or malformed one (and required authentication) staus code 400 (Bad request) will be returned
	
	
### Whitelist filter

This filter is implemented with highest precedence, meaning it will be the first one invoked in the chain of filters. The purpose of implementing it is getting rid of some vulnerabilities coming from the 'Spring' and others. The filter checks if the combination of [request URI + request method + request parameters] is supported and if not then status code 501 (Not implemented) is returned. This way many possible attacks will be prevented, including Cross site scripting(XSS), Buffer overflow etc. 


### HttpRequestLengthInterceptor

This handler interceptor assures that the request length(basically the body of the request - when sending message) is less than a fixed hardcoded size of 64KB. Another validation is of the length of the URL - this is validated against another hardcoded value of 150. This interceptor is implemented with the purpose of stopping requests that are trying to flood the application with too large or malformed data.