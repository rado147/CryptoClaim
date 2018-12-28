package cf.cryptoclaim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CryptoClaimConfiguration {

	 @Bean
	  public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	  }
	
	 /* @Bean
	  @Primary
	  public FilterRegistrationBean<Filter> whitelistFilterRegistration() throws IOException {
	    FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
	    WhitelistFilter whitelistFilter = new WhitelistFilter();
	    registration.setFilter(whitelistFilter);
	    registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
	    return registration;
	  }*/
	
}
