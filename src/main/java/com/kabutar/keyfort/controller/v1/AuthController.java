package com.kabutar.keyfort.controller.v1;

import java.util.*;

import com.kabutar.keyfort.Entity.Client;
import com.kabutar.keyfort.Entity.Session;
import com.kabutar.keyfort.Entity.Token;
import com.kabutar.keyfort.Entity.User;
import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.dto.ClientDto;
import com.kabutar.keyfort.dto.TokenDto;
import com.kabutar.keyfort.dto.UserDto;
import com.kabutar.keyfort.http.Cookie;
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
@RequestMapping("/api/v1/auth/{dimension}")
public class AuthController {

	@Autowired
	private AuthService authService;
	
	private final Logger logger  = LogManager.getLogger(AuthController.class);

	/**
	 *
	 * @param clientId
	 * @param userDto
	 * @return
	 */

	@PostMapping("/login_action")
	public ResponseEntity<?> loginAction(
			@RequestBody UserDto userDto,
			@PathVariable("dimension") String dimension
	){
		logger.info("Entering login_action controller");
		try {
			User user = authService.matchUserCredential(userDto.getUsername(), userDto.getPassword(), userDto.getClientId()       , dimension);
			
			if(user != null){
				Token token = authService.getAuthTokenForUser(user);
				return new ResponseHandler()
						.status(HttpStatus.OK)
						.data(List.of( Map.of("authorizationCode",token.getToken())))
						.build();
			}
			return new ResponseHandler()
					.error(List.of("Invalid credentials!"))
					.status(HttpStatus.UNAUTHORIZED)
					.build();
		}catch(Exception e) {
			return new ResponseHandler()
					.error(List.of(e.getMessage()))
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.build();
		}
	}

	/**
	 *
	 * @param authorization
	 * @param resourceUrl
	 * @return
	 */
	@GetMapping("/token")
	public ResponseEntity<?> validateToken(
			@CookieValue(value="KF_ACCESS_TOKEN") String accessToken,
			@CookieValue(value="KF_REFRESH_TOKEN") String refreshToken,
			@RequestParam("resourceUrl") String resourceUrl,
			@PathVariable("dimension") String dimension
	){

		if(authService.validateAccessToken(accessToken,resourceUrl,dimension)){
			Map<String,Object> tokens = authService.exchangeForTokens(refreshToken, AuthConstant.TokenType.REFRESH, dimension);
			Cookie accessTokenCookie = new Cookie(AuthConstant.CookieType.ACCESS_TOKEN,(String) tokens.get("access"),true,true,"strict",60 * 15);
			Cookie refreshTokenCookie = new Cookie(AuthConstant.CookieType.REFRESH_TOKEN,(String) tokens.get("refresh"),true,true,"strict",60 * 60);
			
			return new ResponseHandler()
					.cookie(refreshTokenCookie)
					.cookie(accessTokenCookie)
					.status(HttpStatus.OK)
					.build();
		}

		return new ResponseHandler()
				.status(HttpStatus.UNAUTHORIZED)
				.build();
	}

	/**
	 *
	 * @param tokenDto
	 * @return
	 */
	@PostMapping("/token")
	public ResponseEntity<?> token(
			@RequestBody TokenDto tokenDto,
			@PathVariable("dimension") String dimension
	){
		try{
			Map<String,Object> tokens = authService.exchangeForTokens(tokenDto.getToken(),tokenDto.getGrantType(), dimension);

			if(!((boolean) tokens.get("isValid"))){
				return new ResponseHandler()
						.status(HttpStatus.UNAUTHORIZED)
						.build();
			}
			
			Cookie accessTokenCookie = new Cookie(AuthConstant.CookieType.ACCESS_TOKEN,(String) tokens.get("access"),true,true,"strict",60 * 15);
			Cookie refreshTokenCookie = new Cookie(AuthConstant.CookieType.REFRESH_TOKEN,(String) tokens.get("refresh"),true,true,"strict",60 * 60);
			

			return new ResponseHandler()
					.cookie(accessTokenCookie)
					.cookie(refreshTokenCookie)
					.status(HttpStatus.OK)
					.build();



		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseHandler()
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.error(List.of(e.getLocalizedMessage()))
					.build();
		}
    }
	
	@PostMapping("/authz_client")
	public ResponseEntity<?> authorizeClient(
			@RequestBody ClientDto client,
			@CookieValue(value="KF_SESSION_ID", required=false) String sessionId,
			@PathVariable("dimension") String dimension
	){

		if(authService.isClientValid(
				client.getClientId(),
				client.getClientSecret(),
				client.getRedirectUri(),
				client.getGrantType(),
				dimension
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
