package com.kabutar.keyfort.controller;

import java.util.*;

import com.kabutar.keyfort.dto.UserLoginDto;
import com.kabutar.keyfort.services.AuthService;
import com.kabutar.keyfort.util.ResponseHandler;
import com.kabutar.keyfort.util.TokenGenerator;
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
	
	private final Logger logger  = LogManager.getLogger(AuthController.class);
	
	@PostMapping("/login_action")
	public ResponseEntity<?> loginAction(
			@RequestParam String clientId,
			@RequestParam String redirectUri,
			@RequestBody UserLoginDto userLoginDto
			){
		if(authService.matchUserCredential(userLoginDto.getUsername(), userLoginDto.getPassword(), clientId)){
			Map<String,String> data = Map.of(
					"authToken", TokenGenerator.generateToken128()
			);
			return new ResponseHandler()
					.status(HttpStatus.OK)
					.data(List.of(data))
					.build();
		}
		return new ResponseHandler()
				.status(HttpStatus.UNAUTHORIZED)
				.build();
	}

	
	@GetMapping("/token")
	public Map<String,String> token(){
//		User user = new User();
//
//		user.setUsername("user");
//
//		user = authService.createUser(user,"dummy-client-id-123456");
//		Credential credential = authService.createCredential(user.getId(),"User@123");
//
//		System.out.println("user: "+user);
//		System.out.printf("credential: "+credential);

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
				.error(List.of("Invalid requester details"))
				.build();
	}
}
