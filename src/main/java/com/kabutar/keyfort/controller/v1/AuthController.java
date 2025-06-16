package com.kabutar.keyfort.controller.v1;

import java.util.*;

import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.data.entity.Token;
import com.kabutar.keyfort.data.entity.User;
import com.kabutar.keyfort.dto.ClientDto;
import com.kabutar.keyfort.dto.TokenDto;
import com.kabutar.keyfort.dto.UserDto;
import com.kabutar.keyfort.security.service.AuthService;
import com.kabutar.keyfort.util.ResponseHandler;

import jakarta.servlet.http.Cookie;

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
			User user = authService.matchUserCredential(userDto.getUsername(), userDto.getPassword());
			
			if((user != null) && (authService.matchRedirectUri(userDto.getClientId(), userDto.getRedirectUri()) != null)){
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
					.error(List.of(e.getLocalizedMessage()))
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.build();
		}
	}

	/**
	 *
	 * @param authcode
	 * @return
	 */
	@PostMapping("/token")
	public ResponseEntity<?> token(
			@RequestBody TokenDto tokenDto,
			@PathVariable("dimension") String dimension
	){
		try{
			Map<String,Object> tokens = authService.exchangeForTokens(tokenDto.getToken(),tokenDto.getClientSecret(), dimension);

			if(!((boolean) tokens.get("isValid"))){
				return new ResponseHandler()
						.error((List<String>)tokens.getOrDefault("errors",List.of("Unexpected error")))
						.status(HttpStatus.UNAUTHORIZED)
						.build();
			}
			
			Cookie accessTokenCookie = new Cookie(AuthConstant.CookieType.ACCESS_TOKEN,(String) tokens.get("access"));
			Cookie refreshTokenCookie = new Cookie(AuthConstant.CookieType.REFRESH_TOKEN,(String) tokens.get("refresh"));
			
			accessTokenCookie.setHttpOnly(true);
			accessTokenCookie.setMaxAge(AuthConstant.ExpiryTime.ACCESS_TOKEN);
			accessTokenCookie.setSecure(true);
			
			refreshTokenCookie.setHttpOnly(true);
			refreshTokenCookie.setMaxAge(AuthConstant.ExpiryTime.REFRESH_TOKEN);
			refreshTokenCookie.setSecure(true);

			return new ResponseHandler()
					.cookie(accessTokenCookie)
					.cookie(refreshTokenCookie)
					.data(List.of(Map.of(
							"accessToken", tokens.get("access"),
							"refreshToken", tokens.get("refresh")
					)))
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

		try {
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
		} catch (Exception e) {
			logger.error("Error occured because of {}",e.getMessage());
			return new ResponseHandler()
					.status(HttpStatus.BAD_REQUEST)
					.error(List.of(e.getLocalizedMessage()))
					.build();
		}

		// all cases failure

		return new ResponseHandler()
				.status(HttpStatus.BAD_REQUEST)
				.error(List.of("Invalid requester details"))
				.build();
	}
}
