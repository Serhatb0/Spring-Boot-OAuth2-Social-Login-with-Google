package com.biricik.springbootsociallogin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biricik.springbootsociallogin.exception.TokenRefreshException;
import com.biricik.springbootsociallogin.model.RefreshToken;
import com.biricik.springbootsociallogin.request.LoginRequest;
import com.biricik.springbootsociallogin.request.RefreshTokenRequest;
import com.biricik.springbootsociallogin.request.SignupRequest;
import com.biricik.springbootsociallogin.response.TokenRefreshResponse;
import com.biricik.springbootsociallogin.service.interfaces.AuthService;
import com.biricik.springbootsociallogin.service.interfaces.RefreshTokenService;

import com.biricik.springbootsociallogin.utils.JwtUtils;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/app")
public class AuthController {

	@Autowired
	AuthService authService;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	RefreshTokenService refreshTokenService;

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(authService.authenticateUser(loginRequest));

	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody SignupRequest signupRequest) {
		return ResponseEntity.ok(authService.registerUser(signupRequest));
	}

	@PostMapping("/refreshtoken")
	public ResponseEntity<?> refreshtoken(@RequestBody RefreshTokenRequest request) {
		String requestRefreshToken = request.getRefreshToken();

		return refreshTokenService.findByToken(requestRefreshToken).map(refreshTokenService::verifyExpiration)
				.map(RefreshToken::getUser).map(user -> {
					String token = jwtUtils.generateTokenFromUsername(user.getUsername());
					return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
				})
				.orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
	}

}
