package com.biricik.springbootsociallogin.service.ımpl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.biricik.springbootsociallogin.model.ERole;
import com.biricik.springbootsociallogin.model.RefreshToken;
import com.biricik.springbootsociallogin.model.Role;
import com.biricik.springbootsociallogin.model.User;
import com.biricik.springbootsociallogin.repository.RoleRepository;
import com.biricik.springbootsociallogin.repository.UserRepository;
import com.biricik.springbootsociallogin.request.LoginRequest;
import com.biricik.springbootsociallogin.request.SignupRequest;
import com.biricik.springbootsociallogin.response.SignupResponse;
import com.biricik.springbootsociallogin.service.interfaces.AuthService;
import com.biricik.springbootsociallogin.service.interfaces.RefreshTokenService;
import com.biricik.springbootsociallogin.utils.JwtUtils;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private RefreshTokenService refreshTokenService;

	@Override
	public SignupResponse authenticateUser(LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		;

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		String jwt = jwtUtils.generateJwtToken(userDetails);

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

		return new SignupResponse(jwt, refreshToken.getToken(), userDetails.getId(), userDetails.getUsername(),
				userDetails.getEmail(), roles);

	}

	@Override
	public String registerUser(SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return "Error: Username is already taken!";
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return "Error: Email is already in use!";
		}

		User user = new User(signUpRequest.getUsername(), passwordEncoder.encode(signUpRequest.getPassword()),
				signUpRequest.getEmail());

		Set<Role> roles = new HashSet<>();
		roles.add(roleRepository.findByName(ERole.ROLE_USER).get());

		user.setRoles(roles);
		userRepository.save(user);

		return "User registered successfully!";

	}

}
