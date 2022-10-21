package com.biricik.springbootsociallogin.security.oauth2;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.biricik.springbootsociallogin.config.AppProperties;
import com.biricik.springbootsociallogin.exception.BadRequestException;
import com.biricik.springbootsociallogin.utils.CookieUtils;
import com.biricik.springbootsociallogin.utils.JwtUtils;

import lombok.extern.slf4j.Slf4j;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	


	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private AppProperties appProperties;

	@Autowired
	private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		String targetUrl = determineTargetUrl(request, response, authentication);

		clearAuthenticationAttributes(request, response);
		System.out.println("onAuthenticationSuccess: targetUrl: " + targetUrl);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);

	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
	}

	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		Optional<String> redirectUri = CookieUtils
				.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
				.map(Cookie::getValue);

		System.out.println("determineTargetUrl: redirectUri: " + redirectUri.get().toString());

		if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
			throw new BadRequestException(
					"Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
		}

		String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

		System.out.println("determineTargetUrl: target: " + targetUrl);
		

		String token = jwtUtils.generateJwtToken(authentication);

		String uriBuilder = UriComponentsBuilder.fromUriString(targetUrl).queryParam("token", token).build()
				.toUriString();

		System.out.println("determineTargetUrl: uriBuilder: " + uriBuilder);

		return uriBuilder;
	}

	private boolean isAuthorizedRedirectUri(String uri) {
		URI clientRedirectUri = URI.create(uri);

		System.out.println("isAuthorizedRedirectUri:clientRedirectUri: " + uri);
		System.out.println("isAuthorizedRedirectUri:clientRedirectUri get Host: " + clientRedirectUri.getHost());
		System.out.println("isAuthorizedRedirectUri:clientRedirectUri get Port: " + clientRedirectUri.getPort());
		return appProperties.getOauth2().getAuthorizedRedirectUris().stream().anyMatch(authorizedRedirectUri -> {
			URI authorizedURI = URI.create(authorizedRedirectUri);
			System.out.println("isAuthorizedRedirectUri:authorizedURI: " + authorizedURI);
			System.out.println("isAuthorizedRedirectUri:authorizedURI get Host: " + authorizedURI.getHost());
			System.out.println("isAuthorizedRedirectUri:authorizedURI get Port: " + authorizedURI.getPort());
			if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
					&& authorizedURI.getPort() == clientRedirectUri.getPort()) {
				return true;
			}
			return false;
		});

	}

}
