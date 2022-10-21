package com.biricik.springbootsociallogin.utils;

import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.biricik.springbootsociallogin.config.AppProperties;
import com.biricik.springbootsociallogin.service.ımpl.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtils {

	private final AppProperties appProperties;

	public JwtUtils(AppProperties appProperties) {
		this.appProperties = appProperties;
	}

	public String generateJwtToken(UserDetailsImpl userDetailsImpl) {
		return generateTokenFromUsername(userDetailsImpl.getUsername());
	}

	public String generateJwtToken(Authentication authentication) {
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
		return generateTokenFromUsername(userDetailsImpl.getUsername());

	}

	public String generateTokenFromUsername(String username) {
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + appProperties.getAuth().getJwtExpirationMs()))
				.signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getJwtSecret()).compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(appProperties.getAuth().getJwtSecret()).parseClaimsJws(token).getBody()
				.getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(appProperties.getAuth().getJwtSecret()).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

}
