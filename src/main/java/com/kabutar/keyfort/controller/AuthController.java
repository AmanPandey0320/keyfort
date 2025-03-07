package com.kabutar.keyfort.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@PostMapping("/login_action")
	public Map<String,String> loginAction(){
		return Map.of("authCode","some_secret_auth_code");
	}
	
	@GetMapping("/token")
	public Map<String,String> token(){
		return Map.of("authToken","some_jwt_auth_token");
	}
	
	@PostMapping("/authz_client")
	public ResponseEntity<?> authorizeClient(){
		return ResponseEntity.ok(null);
	}
}
