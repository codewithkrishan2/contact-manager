package com.contact.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class MyConfig {
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity  httpSecurity) {
	
		DefaultSecurityFilterChain defaultSecurityFilterChain = http.build();
		return defaultSecurityFilterChain;
	}
	
}
