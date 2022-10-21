package com.biricik.springbootsociallogin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.biricik.springbootsociallogin.model.RefreshToken;
import com.biricik.springbootsociallogin.model.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token);

	@Modifying
	int deleteByUser(User user);
}
