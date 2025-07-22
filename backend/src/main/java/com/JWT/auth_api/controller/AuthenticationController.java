package com.JWT.auth_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.JWT.auth_api.model.AuthenticationResponse;
import com.JWT.auth_api.model.User;
import com.JWT.auth_api.repository.UserRepository;
import com.JWT.auth_api.service.AuthenticationService;

@RestController
//@CrossOrigin(origins="*")
public class AuthenticationController {
	
	private final UserRepository userRepository;
	private final AuthenticationService authService;

	public AuthenticationController(AuthenticationService authService,UserRepository userRepository) {
		this.authService = authService;
		this.userRepository=userRepository;
	}
	
	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(
			@RequestBody User request
	){
		return ResponseEntity.ok(authService.register(request));
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponse> login ( 
			@RequestBody User request
	){
		return ResponseEntity.ok(authService.authenticate(request)); // ✅ Fixed method call
	}
	
	@GetMapping("/ping")
	public ResponseEntity<String> ping() {
	  return ResponseEntity.ok("pong");
	}
	
	@GetMapping("/users")
	public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
	
	
}
