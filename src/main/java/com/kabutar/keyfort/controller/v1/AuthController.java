package com.kabutar.keyfort.controller.v1;

import java.util.*;

import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.data.entity.Token;
import com.kabutar.keyfort.data.entity.User;
import com.kabutar.keyfort.dto.ClientDto;
import com.kabutar.keyfort.dto.TokenDto;
import com.kabutar.keyfort.dto.UserDto;
import com.kabutar.keyfort.http.ResponseFactory;
import com.kabutar.keyfort.security.interfaces.SecureAuthFlow;
import com.kabutar.keyfort.security.service.AuthService;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/api/v1/auth/{dimension}")
public class AuthController {

	@Autowired
	private AuthService authService;
	
	@Autowired
	private SecureAuthFlow authFlow;
	
	private final Logger logger  = LogManager.getLogger(AuthController.class);

	/**
	 *
	 * @param clientId
	 * @param userDto
	 * @return
	 */

	@PostMapping("/login_action")
	public Mono<ResponseEntity<?>> loginAction(
			@RequestBody UserDto userDto,
			@PathVariable("dimension") String dimension,
			ServerWebExchange exchange
	){
		logger.info("Entering login_action controller");
		
		String sessionId = exchange.getAttributeOrDefault(AuthConstant.CookieType.SESSION_ID, null);
		
		if(sessionId == null) {
			return new ResponseFactory()
					.error(List.of("No valid session found"))
					.status(HttpStatus.UNAUTHORIZED)
					.build();
		}
		
		try {
			User user = authService.matchUserCredential(userDto.getUsername(), userDto.getPassword());
			
			if((user != null) && (authService.matchRedirectUri(userDto.getClientId(), userDto.getRedirectUri()) != null)){
				Token token = authService.getAuthTokenForUser(user);
				this.authFlow.init(sessionId, userDto.getCodeChallange());
				return new ResponseFactory()
						.status(HttpStatus.OK)
						.data(List.of( Map.of("authorizationCode",token.getToken())))
						.build();
			}
			return new ResponseFactory()
					.error(List.of("Invalid credentials!"))
					.status(HttpStatus.UNAUTHORIZED)
					.build();
		}catch(Exception e) {
			return new ResponseFactory()
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
	public Mono<ResponseEntity<?>> token(
			@RequestBody TokenDto tokenDto,
			@PathVariable("dimension") String dimension,
			ServerWebExchange exchange
	){
		try{
			String sessionId = exchange.getAttributeOrDefault(AuthConstant.CookieType.SESSION_ID, null);
			Map<String,Object> tokens = authService.exchangeForTokens(tokenDto.getToken(),tokenDto.getClientSecret(), dimension,sessionId);

			if(!((boolean) tokens.get("isValid"))){
				return new ResponseFactory()
						.error((List<String>)tokens.getOrDefault("errors",List.of("Unexpected error")))
						.status(HttpStatus.UNAUTHORIZED)
						.build();
			}
			
			if(!this.authFlow.verify(sessionId, tokenDto.getCodeVerifier())) {
				return new ResponseFactory()
						.error(List.of("Invalid requester"))
						.status(HttpStatus.BAD_REQUEST)
						.build();
			}
			
			ResponseCookie accessTokenCookie = ResponseCookie.from(AuthConstant.CookieType.ACCESS_TOKEN,(String)tokens.get("access"))
					.httpOnly(true)
					.path("/")
					.maxAge(AuthConstant.ExpiryTime.ACCESS_TOKEN)
	                .build();
			
			ResponseCookie refreshTokenCookie = ResponseCookie.from(AuthConstant.CookieType.ACCESS_TOKEN,(String)tokens.get("refresh"))
					.httpOnly(true)
					.path("/")
					.maxAge(AuthConstant.ExpiryTime.REFRESH_TOKEN)
	                .build();

			return new ResponseFactory()
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
			return new ResponseFactory()
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.error(List.of(e.getLocalizedMessage()))
					.build();
		}
    }
	
	@PostMapping("/authz_client")
	public Mono<ResponseEntity<?>> authorizeClient(
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
				return new ResponseFactory()
						.status(HttpStatus.OK)
						.build();
			}
		} catch (Exception e) {
			logger.error("Error occured because of {}",e.getMessage());
			return new ResponseFactory()
					.status(HttpStatus.BAD_REQUEST)
					.error(List.of(e.getLocalizedMessage()))
					.build();
		}

		// all cases failure

		return new ResponseFactory()
				.status(HttpStatus.BAD_REQUEST)
				.error(List.of("Invalid requester details"))
				.build();
	}
}
