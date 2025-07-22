package com.JWT.auth_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
	
	@GetMapping("/demo")
	public ResponseEntity<String> demo()
	{
		return ResponseEntity.ok("Hello");
	}
	
	@GetMapping("/admin_only")
	public ResponseEntity<String> adminOnly()
	{
		return ResponseEntity.ok("Hello admin!");
	}
}
