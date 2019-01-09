package cf.cryptoclaim.config;

import java.io.IOException;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;

import cf.cryptoclaim.auth.CryptoClaimAuthenticationFilter;
import cf.cryptoclaim.controller.HttpRequestLengthInterceptor;
import cf.cryptoclaim.whitelist.WhitelistFilter;

@Configuration
public class CryptoClaimConfiguration {
	
	  @Bean
	  @Primary
	  public FilterRegistrationBean<Filter> whitelistFilterRegistration() throws IOException {
	    FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
	    WhitelistFilter whitelistFilter = new WhitelistFilter();
	    registration.setFilter(whitelistFilter);
	    registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
	    return registration;
	  }

	  @Bean
	  public FilterRegistrationBean<Filter> authenticationFilterRegistration() throws IOException {
	    FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
	    CryptoClaimAuthenticationFilter cryptoClaimAuthenticationFilter = new CryptoClaimAuthenticationFilter();
	    registration.setFilter(cryptoClaimAuthenticationFilter);
	    return registration;
	  }
	  
	  @Bean
	  public HttpRequestLengthInterceptor httpRequestLengthInterceptor() {
	    return new HttpRequestLengthInterceptor();
	  }
}
