package com.JWT.auth_api.service;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.JWT.auth_api.model.AuthenticationResponse;
import com.JWT.auth_api.model.User;
import com.JWT.auth_api.repository.UserRepository;

@Service
public class AuthenticationService {
	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	
	
	
	public AuthenticationService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService,
			AuthenticationManager authenticationManager) {
		super();
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
	}

	public AuthenticationResponse register(User request)
	{
		User user= new User();
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		
		user.setRole(request.getRole());
		
		user=repository.save(user);
		
		String token= jwtService.generateToken(user);
		
//		return new AuthenticationResponse(token); 
		
		
		//new
		return new AuthenticationResponse(token, user.getRole().name());
		//new
	}
	
	public AuthenticationResponse authenticate(User request)
	{
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getUsername(),
						request.getPassword()
						)
				);
		User user=repository.findByUsername(request.getUsername()).orElseThrow();
		String token= jwtService.generateToken(user);
		
//old		return new AuthenticationResponse(token);
		//new
		return new AuthenticationResponse(token, user.getRole().name());
		//new
	}
	
	
}
