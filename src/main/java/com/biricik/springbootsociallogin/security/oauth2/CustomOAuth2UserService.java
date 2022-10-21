package com.biricik.springbootsociallogin.security.oauth2;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.biricik.springbootsociallogin.exception.OAuth2AuthenticationProcessingException;
import com.biricik.springbootsociallogin.model.AuthProvider;
import com.biricik.springbootsociallogin.model.User;
import com.biricik.springbootsociallogin.repository.UserRepository;
import com.biricik.springbootsociallogin.security.oauth2.user.OAuth2UserInfo;
import com.biricik.springbootsociallogin.security.oauth2.user.OAuth2UserInfoFactory;
import com.biricik.springbootsociallogin.service.ımpl.UserDetailsImpl;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	
	@Autowired
	private UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);

		try {
			return processOAuth2User(userRequest, oAuth2User);
		} catch (AuthenticationException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
		}

	}

	private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {

		OAuth2UserInfo oauth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
				oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());

		System.out.println("getRedirectUri: " + oAuth2UserRequest.getClientRegistration().getRedirectUri());
		System.out.println("getRedirectUriTemplate: " + oAuth2UserRequest.getClientRegistration().getRedirectUriTemplate());
		System.out.println("getRegistrationId: " + oAuth2UserRequest.getClientRegistration().getRegistrationId());
		System.out.println("getClientName: " + oAuth2UserRequest.getClientRegistration().getClientName());

		if (StringUtils.isEmpty(oauth2UserInfo.getEmail())) {
			throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
		}

		Optional<User> userOptional = userRepository.findByEmail(oauth2UserInfo.getEmail());
		User user;
		if (userOptional.isPresent()) {
			user = userOptional.get();
			if (!user.getProvider()
					.equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {

				throw new OAuth2AuthenticationProcessingException(
						"Looks like you're signed up with " + user.getProvider() + " account. Please use your "
								+ user.getProvider() + " account to login.");

			}
			;
			user = updateExistingUser(user, oauth2UserInfo);

		} else {
			user = registerNewUser(oAuth2UserRequest, oauth2UserInfo);
		}

		return UserDetailsImpl.build(user, oauth2UserInfo.getAttributes());
	}

	private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
		User user = new User();

		user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
		user.setProviderId(oAuth2UserInfo.getId());
		user.setUsername(oAuth2UserInfo.getName());
		user.setEmail(oAuth2UserInfo.getEmail());
		user.setImageUrl(oAuth2UserInfo.getImageUrl());
		return userRepository.save(user);
	}

	private User updateExistingUser(User user, OAuth2UserInfo oAuth2UserInfo) {
		user.setUsername(oAuth2UserInfo.getName());
		user.setImageUrl(oAuth2UserInfo.getImageUrl());
		return userRepository.save(user);
	}

}
