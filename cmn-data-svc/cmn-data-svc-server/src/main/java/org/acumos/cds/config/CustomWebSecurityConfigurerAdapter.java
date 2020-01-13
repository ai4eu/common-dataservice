/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.cds.config;

import org.acumos.cds.CCDSConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

/**
 * Spring 4 security requires a CSRF token on POST/PUT/DELETE requests. But this
 * server has no web pages where a CSRF token would be sent, so disable.
 * 
 * Use basic HTTP auth, but exclude the health check from Spring security.
 * 
 * With credit to:
 * http://ryanjbaxter.com/2015/01/06/securing-rest-apis-with-spring-boot/
 */
@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

	private static final String REALM_NAME = "Acumos-CDS";

	/**
	 * Open access to the documentation.
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**");
	}

	/**
	 * Open access to the health and version endpoints.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable() //
				.authorizeRequests() //
				.antMatchers("/" + CCDSConstants.HEALTHCHECK_PATH).permitAll() //
				.antMatchers("/" + CCDSConstants.VERSION_PATH).permitAll() //
				.antMatchers("/**").authenticated() //
				.and().httpBasic().realmName(REALM_NAME).authenticationEntryPoint(getBasicAuthEntryPoint());
	}

	@Bean
	public BasicAuthenticationEntryPoint getBasicAuthEntryPoint() {
		BasicAuthenticationEntryPoint baep = new CustomBasicAuthenticationEntryPoint();
		baep.setRealmName(REALM_NAME);
		return baep;
	}

}
