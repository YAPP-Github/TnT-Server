package com.tnt.gateway.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import com.tnt.gateway.filter.ServletExceptionFilter;
import com.tnt.gateway.filter.SessionAuthenticationFilter;
import com.tnt.gateway.service.CustomOAuth2UserService;
import com.tnt.gateway.service.SessionService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private static final String[] ALLOWED_URIS = {
		"/",
		"/oauth2/**",
		"/login/**",
		"/api",
		"/v3/api-docs/**",
		"/swagger-ui/**",
		"/members/sign-up"
	};

	private final CustomOAuth2UserService customOAuth2UserService;
	private final SessionService sessionService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(Customizer.withDefaults())
			.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
			.sessionManagement(sessionManagement ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.oauth2Login(oauth2 -> oauth2.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)))
			.authorizeHttpRequests(request -> request
				.requestMatchers(ALLOWED_URIS).permitAll().anyRequest().authenticated())
			.addFilterBefore(servletExceptionFilter(), LogoutFilter.class)
			.addFilterAfter(sessionAuthenticationFilter(), LogoutFilter.class)
			.exceptionHandling(exceptionHandling ->
				exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
					log.error("SecurityFilter Exception.", authException);
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType("application/json;charset=UTF-8");
					response.getWriter().write("{\"message\":\"Security 사용자 인증에 실패했습니다.\"}");
				})
			);

		return http.build();
	}

	@Bean
	public ServletExceptionFilter servletExceptionFilter() {
		return new ServletExceptionFilter();
	}

	@Bean
	public SessionAuthenticationFilter sessionAuthenticationFilter() {
		return new SessionAuthenticationFilter(Arrays.asList(ALLOWED_URIS), sessionService);
	}
}
