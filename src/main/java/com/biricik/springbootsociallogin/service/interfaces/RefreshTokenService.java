package com.biricik.springbootsociallogin.service.interfaces;

import java.util.Optional;

import com.biricik.springbootsociallogin.model.RefreshToken;


public interface RefreshTokenService {
	
	
	Optional<RefreshToken> findByToken(String token);
	

	
	int deleteByUserId(Long userId);
	
	RefreshToken verifyExpiration(RefreshToken token);

	RefreshToken createRefreshToken(Long id);
	


}
