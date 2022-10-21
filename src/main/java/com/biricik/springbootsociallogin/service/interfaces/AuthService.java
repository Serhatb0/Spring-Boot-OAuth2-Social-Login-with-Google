package com.biricik.springbootsociallogin.service.interfaces;

import com.biricik.springbootsociallogin.request.LoginRequest;
import com.biricik.springbootsociallogin.request.RefreshTokenRequest;
import com.biricik.springbootsociallogin.request.SignupRequest;
import com.biricik.springbootsociallogin.response.SignupResponse;
import com.biricik.springbootsociallogin.response.TokenRefreshResponse;

public interface AuthService {

	SignupResponse authenticateUser(LoginRequest loginRequest);
	
	
	String registerUser(SignupRequest signUpRequest);
	
	
	
	
	
}
