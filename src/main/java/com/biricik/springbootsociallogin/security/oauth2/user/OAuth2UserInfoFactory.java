package com.biricik.springbootsociallogin.security.oauth2.user;

import java.util.Map;

import org.springframework.security.oauth2.client.registration.ClientRegistration;

import com.biricik.springbootsociallogin.exception.OAuth2AuthenticationProcessingException;
import com.biricik.springbootsociallogin.model.AuthProvider;

public class OAuth2UserInfoFactory {

	public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
		if (registrationId.equalsIgnoreCase(AuthProvider.google.toString())) {
			return new GoogleOAuth2UserInfo(attributes);
		} else {
			throw new OAuth2AuthenticationProcessingException(
					"Sorry! Login with " + registrationId + " is not supported yet.");
		}
	}

	
}
