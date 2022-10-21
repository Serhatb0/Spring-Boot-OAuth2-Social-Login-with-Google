package com.biricik.springbootsociallogin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.biricik.springbootsociallogin.security.AuthenticationEntryPointJwt;
import com.biricik.springbootsociallogin.security.AuthenticationTokenFilter;
import com.biricik.springbootsociallogin.security.oauth2.CustomOAuth2UserService;
import com.biricik.springbootsociallogin.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.biricik.springbootsociallogin.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.biricik.springbootsociallogin.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.biricik.springbootsociallogin.service.ımpl.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)public class WebSecurityConfig {

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthenticationEntryPointJwt unauthorizedHandler;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CustomOAuth2UserService customOAuth2UserService;

	@Autowired
	private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

	@Autowired
	private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

	@Autowired
	private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

	@Bean
	public AuthenticationTokenFilter authenticationJwtTokenFilter() {
		return new AuthenticationTokenFilter();
	}

	@Bean
	public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
		return new HttpCookieOAuth2AuthorizationRequestRepository();
	}

	@Bean
	public AuthenticationManager authManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder).and().build();
	}

	@Bean()
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers("/app/**").permitAll().antMatchers("/app/login").permitAll()
				.antMatchers("/", "/error", "/favicon.ico", "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg",
						"/**/*.html", "/**/*.css", "/**/*.js")
				.permitAll().antMatchers("/auth/**", "/oauth2/**").permitAll()
				.antMatchers("/cookie/*", "/cookie/read", "/cookie/delete").permitAll().anyRequest().authenticated()
				.and().oauth2Login().authorizationEndpoint().baseUri("/oauth2/authorize")
				.authorizationRequestRepository(cookieAuthorizationRequestRepository()).and().redirectionEndpoint()
				.baseUri("/oauth2/callback/*").and().userInfoEndpoint().userService(customOAuth2UserService).and()
				.successHandler(oAuth2AuthenticationSuccessHandler).failureHandler(oAuth2AuthenticationFailureHandler);

		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();

	}

	/*
	 * 
	 * @Bean public AuthenticationManager configure(AuthenticationManagerFactoryBean
	 * authenticationManagerFactoryBean, AuthenticationManagerBuilder
	 * authenticationManagerBuilder) throws Exception {
	 * authenticationManagerBuilder.userDetailsService(userDetailsService).
	 * passwordEncoder(passwordEncoder); return
	 * authenticationManagerFactoryBean.setBeanFactory(authenticationManagerBuilder)
	 * ;; }
	 * 
	 * @Override protected void configure(HttpSecurity http) throws Exception {
	 * http.cors().and().csrf().disable()
	 * .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
	 * .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
	 * and() .authorizeRequests().antMatchers("/app/**").permitAll()
	 * .antMatchers("/app/login").permitAll()
	 * .antMatchers("/app/test/**").permitAll() .anyRequest().authenticated();
	 * 
	 * http.addFilterBefore(authenticationJwtTokenFilter(),
	 * UsernamePasswordAuthenticationFilter.class); }
	 */

}
