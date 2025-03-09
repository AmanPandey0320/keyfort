package com.kabutar.keyfort.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.kabutar.keyfort.Entity.Credential;
import com.kabutar.keyfort.Entity.User;
import com.kabutar.keyfort.dto.UserLoginDto;
import com.kabutar.keyfort.repository.ClientRepository;
import com.kabutar.keyfort.services.AuthService;
import com.kabutar.keyfort.util.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kabutar.keyfort.Entity.Client;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthService authService;
	
	private Logger logger  = LogManager.getLogger(AuthController.class);
	
//	@PostMapping("/login_action")
//	public ResponseEntity<?> loginAction(
//			@RequestParam String clientId,
//			@RequestParam String redirectUri,
//			@RequestBody UserLoginDto userLoginDto
//			){
//
//	}

	
	@GetMapping("/token")
	public Map<String,String> token(){
		User user = new User();

		user.setUsername("user");

		user = authService.createUser(user,"dummy-client-id-123456");
		Credential credential = authService.createCredential(user.getId(),"User@123");

		System.out.println("user: "+user);
		System.out.printf("credential: "+credential);

		return Map.of("authToken","some_jwt_auth_token");
	}
	
	@PostMapping("/authz_client")
	public ResponseEntity<?> authorizeClient(
			@RequestBody Client client
			){

		if(authService.isClientValid(client)){
			//success
			logger.info("Client with id: {}, requested authorization",client.getClientId());
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
