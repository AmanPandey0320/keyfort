package com.kabutar.keyfort.controller.v1;

import java.util.*;

import com.kabutar.keyfort.Entity.Token;
import com.kabutar.keyfort.Entity.User;
import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.dto.ClientDto;
import com.kabutar.keyfort.dto.TokenDto;
import com.kabutar.keyfort.dto.UserDto;
import com.kabutar.keyfort.service.AuthService;
import com.kabutar.keyfort.service.JwtService;
import com.kabutar.keyfort.util.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private JwtService jwtService;
	
	private final Logger logger  = LogManager.getLogger(AuthController.class);

	/**
	 *
	 * @param clientId
	 * @param userDto
	 * @return
	 */

	@PostMapping("/login_action")
	public ResponseEntity<?> loginAction(
			@RequestParam String clientId,
			@RequestBody UserDto userDto
	){
		User user = authService.matchUserCredential(userDto.getUsername(), userDto.getPassword(), clientId);
		if(user != null){
			Token token = authService.getAuthTokenForUser(user);
			return new ResponseHandler()
					.status(HttpStatus.OK)
					.data(
							List.of( Map.of("authorizationCode",token.getToken()) )
					)
					.build();
		}
		return new ResponseHandler()
				.status(HttpStatus.UNAUTHORIZED)
				.build();
	}

	
	@PostMapping("/token")
	public ResponseEntity<?> token(
			@RequestBody TokenDto tokenDto
	){
		try{
			Token token = authService.getTokenForAuthCode(tokenDto.getToken());

			if(token == null || (token.getValidTill().getTime() < System.currentTimeMillis())){
				//invalid auth code
				return new ResponseHandler()
						.status(HttpStatus.BAD_REQUEST)
						.error(List.of("The authorization code is either invalid or has expired"))
						.build();
			}else if(!token.getType().equals(tokenDto.getGrantType())){
				//invalid auth code
				return new ResponseHandler()
						.status(HttpStatus.FORBIDDEN)
						.error(List.of("Invalid grant"))
						.build();
			}

			//valid auth code
			//send token

			User user = token.getUser();
			String accessToken = jwtService.generateToken(
					Map.of(AuthConstant.ROLE,List.of("default")),
					user.getUsername(),
					AuthConstant.ExpiryTime.ACCESS_TOKEN
			);

			String refreshToken = jwtService.generateToken(
					Map.of(
							AuthConstant.ROLE,List.of("default")

					),
					user.getUsername(),
					AuthConstant.ExpiryTime.REFRESH_TOKEN
			);

			return new ResponseHandler()
					.status(HttpStatus.OK)
					.data(List.of(
							Map.of(
									"access_token",accessToken,
									"refresh_token",refreshToken,
									"expires_in", AuthConstant.ExpiryTime.ACCESS_TOKEN
							)
					))
					.build();

		} catch (Exception e) {
			logger.error(e.getStackTrace());
			return new ResponseHandler()
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.error(List.of(e.getLocalizedMessage()))
					.build();
		}
    }
	
	@PostMapping("/authz_client")
	public ResponseEntity<?> authorizeClient(
			@RequestBody ClientDto client
	){

		if(authService.isClientValid(
				client.getClientId(),
				client.getClientSecret(),
				client.getRedirectUri(),
				client.getGrantType()
		)){
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
