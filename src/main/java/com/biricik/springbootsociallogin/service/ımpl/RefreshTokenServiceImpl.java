package com.biricik.springbootsociallogin.service.ımpl;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.biricik.springbootsociallogin.config.AppProperties;
import com.biricik.springbootsociallogin.exception.TokenRefreshException;
import com.biricik.springbootsociallogin.model.RefreshToken;
import com.biricik.springbootsociallogin.repository.RefreshTokenRepository;
import com.biricik.springbootsociallogin.repository.UserRepository;
import com.biricik.springbootsociallogin.request.RefreshTokenRequest;
import com.biricik.springbootsociallogin.response.TokenRefreshResponse;
import com.biricik.springbootsociallogin.service.interfaces.RefreshTokenService;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

	

	private final AppProperties appProperties;

	private final RefreshTokenRepository refreshTokenRepository;

	private final UserRepository userRepository;

	public RefreshTokenServiceImpl(AppProperties appProperties, RefreshTokenRepository refreshTokenRepository,
			UserRepository userRepository) {

		this.appProperties = appProperties;
		this.refreshTokenRepository = refreshTokenRepository;
		this.userRepository = userRepository;
	}
	


	@Override
	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	@Override
	public RefreshToken createRefreshToken(Long userId) {
		RefreshToken refreshToken = new RefreshToken();

		refreshToken.setUser(userRepository.findById(userId).get());
		refreshToken.setExpiryDate(Instant.now().plusMillis(appProperties.getAuth().getJwtRefreshExpirationMs()));
		refreshToken.setToken(UUID.randomUUID().toString());
		return refreshTokenRepository.save(refreshToken);
	}

	@Override
	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.delete(token);
			throw new TokenRefreshException(token.getToken(),
					"Refresh token was expired. please make a new signin request");
		}

		return token;
	}

	@Override
	@Transactional
	public int deleteByUserId(Long userId) {
		return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
	}

}
