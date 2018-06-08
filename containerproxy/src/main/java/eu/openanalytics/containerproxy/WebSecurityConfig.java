/**
 * ShinyProxy
 *
 * Copyright (C) 2016-2018 Open Analytics
 *
 * ===========================================================================
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License as published by
 * The Apache Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License for more details.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/>
 */
package eu.openanalytics.containerproxy;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import eu.openanalytics.containerproxy.auth.IAuthenticationBackend;
import eu.openanalytics.containerproxy.auth.UserLogoutHandler;
import eu.openanalytics.containerproxy.service.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Inject
	private UserLogoutHandler logoutHandler;

	@Inject
	private UserService userService;
	
	@Inject
	private IAuthenticationBackend auth;
	
	@Inject
	private AuthenticationEventPublisher eventPublisher;
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web
			.ignoring().antMatchers("/css/**").and()
			.ignoring().antMatchers("/webjars/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			// must disable or handle in proxy
			.csrf().disable()
			// disable X-Frame-Options
			.headers().frameOptions().disable();

		if (auth.hasAuthorization()) {
			// Limit access to the app pages
			http.authorizeRequests().antMatchers("/login").permitAll();
			
//			http.authorizeRequests().antMatchers("/login", "/signin/**", "/signup").permitAll();
			//TODO ShinyProxy specific
//			for (ProxySpec spec: proxyService.listSpecs()) {
//				if (spec.getAccessControl() == null) continue;
//				String[] groups = spec.getAccessControl().getGroups();
//				if (groups == null || groups.length == 0) continue;
//				String[] appGroups = Arrays.stream(groups).map(s -> s.toUpperCase()).toArray(i -> new String[i]);
//				http.authorizeRequests().antMatchers("/app/" + spec.getName()).hasAnyRole(appGroups);
//			}

			// Limit access to the admin pages
			http.authorizeRequests().antMatchers("/admin").hasAnyRole(userService.getAdminGroups());
			
			// All other pages are available to authenticated users
			http.authorizeRequests().anyRequest().fullyAuthenticated();

			http
				.formLogin()
					.loginPage("/login")
					.and()
				.logout()
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
					.addLogoutHandler(logoutHandler)
					.logoutSuccessUrl("/login");
			
			// Enable basic auth for RESTful calls
			//TODO Restful calls with basic auth will generate a new "login" every time
			http.addFilter(new BasicAuthenticationFilter(authenticationManagerBean()));
		}
		
		auth.configureHttpSecurity(http);
	}

	@Bean
	public GlobalAuthenticationConfigurerAdapter authenticationConfiguration() {
		return new GlobalAuthenticationConfigurerAdapter() {
			@Override
			public void init(AuthenticationManagerBuilder amb) throws Exception {
				amb.authenticationEventPublisher(eventPublisher);
				auth.configureAuthenticationManagerBuilder(amb);
			}
		};
	}
	
	@Bean(name="authenticationManager")
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}