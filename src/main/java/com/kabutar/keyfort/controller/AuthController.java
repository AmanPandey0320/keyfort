package com.kabutar.keyfort.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.kabutar.keyfort.repository.ClientRepository;
import com.kabutar.keyfort.util.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kabutar.keyfort.Entity.Client;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	ClientRepository clientRepository;
	
	private Logger logger  = LogManager.getLogger(AuthController.class);
	
	@PostMapping("/login_action")
	public Map<String,String> loginAction(){
		return Map.of("authCode","some_secret_auth_code");
	}
	
	@GetMapping("/token")
	public Map<String,String> token(){
		return Map.of("authToken","some_jwt_auth_token");
	}
	
	@PostMapping("/authz_client")
	public ResponseEntity<?> authorizeClient(
			@RequestBody Client client
			){
		Client savedClient = clientRepository.findByClientId(client.getClientId());

		if(
				savedClient.getClientSecret().equals(client.getClientSecret()) &&
				savedClient.getRedirectUri().equals(client.getRedirectUri()) &&
				savedClient.getGrantType().equals(client.getGrantType())
		){
			//success
			return new ResponseHandler()
					.status(HttpStatus.OK)
					.build();
		}

		// all cases failure

		return new ResponseHandler()
				.status(HttpStatus.BAD_REQUEST)
				.error(Arrays.asList("Invalid requester details"))
				.build();
	}
}
