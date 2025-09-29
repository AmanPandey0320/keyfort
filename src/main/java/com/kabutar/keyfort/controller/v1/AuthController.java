package com.kabutar.keyfort.controller.v1;

import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.data.entity.User;
import com.kabutar.keyfort.dto.ClientDto;
import com.kabutar.keyfort.dto.RefreshTokenDto;
import com.kabutar.keyfort.dto.TokenDto;
import com.kabutar.keyfort.dto.UserDto;
import com.kabutar.keyfort.http.ResponseFactory;
import com.kabutar.keyfort.security.interfaces.SecureAuthFlow;
import com.kabutar.keyfort.security.service.AuthService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/auth/{dimension}")
public class AuthController {
    private final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private SecureAuthFlow authFlow;

    /**
     * @param clientId
     * @param userDto
     * @return
     */
    @PostMapping("/login_action")
    public Mono<ResponseEntity<?>> loginAction(
            @RequestBody UserDto userDto,
            @PathVariable("dimension") String dimension,
            ServerWebExchange exchange) {

        logger.info("Entering login_action controller");

        String sessionId = exchange.getAttributeOrDefault(AuthConstant.CookieType.SESSION_ID, null);

        if (sessionId == null) {
            logger.debug("No valid session found");
            return new ResponseFactory()
                    .error(List.of("No valid session found"))
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        Mono<User> userMono =
                authService.matchUserCredential(userDto.getUsername(), userDto.getPassword());

        return userMono.flatMap(user -> {
            logger.debug("User found with details: {}", user.toString());
            try {
                return authService
                        .matchRedirectUri(userDto.getClientId(), userDto.getRedirectUri())
                        .hasElement()
                        .flatMap(isValid -> {
                            if (isValid) {
                                try {
                                    return authService.getAuthTokenForUser(user).flatMap(token -> {
                                        // init auth flowflatMap()
                                        this.authFlow
                                                .init(sessionId, userDto.getCodeChallange())
                                                .subscribe(
                                                        d -> {
                                                            logger.debug(
                                                                    "Auth flow initiated for session {}",
                                                                    sessionId);
                                                        },
                                                        e -> {
                                                            logger.error(
                                                                    "Error occured while initializing authflow, reason: {}",
                                                                    e.getLocalizedMessage());
                                                            logger.debug("Authflow error: ", e);
                                                        });

                                        return new ResponseFactory()
                                                .status(HttpStatus.OK)
                                                .data(List.of(Map.of(
                                                        "authorizationCode", token.getToken())))
                                                .build();
                                    });
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            return new ResponseFactory()
                                    .status(HttpStatus.UNAUTHORIZED)
                                    .build();
                        });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * @param authcode
     * @return
     */
    @PostMapping("/exchange_token")
    public Mono<ResponseEntity<?>> exchangeForTokens(
            @RequestBody TokenDto tokenDto,
            @PathVariable("dimension") String dimension,
            ServerWebExchange exchange) {

        String sessionId = exchange.getAttributeOrDefault(AuthConstant.CookieType.SESSION_ID, null);
        return authService
                .exchangeForTokens(
                        tokenDto.getToken(), tokenDto.getClientSecret(), dimension, sessionId)
                .flatMap(tokens -> {
                    if (!((boolean) tokens.get("isValid"))) {
                        return new ResponseFactory()
                                .error((List<String>)
                                        tokens.getOrDefault("errors", List.of("Unexpected error")))
                                .status(HttpStatus.UNAUTHORIZED)
                                .build();
                    }

                    return this.authFlow
                            .verify(sessionId, tokenDto.getCodeVerifier())
                            .flatMap(isVerified -> {
                                if (!isVerified) {
                                    return new ResponseFactory()
                                            .error(List.of("Invalid requester or code verifier"))
                                            .status(HttpStatus.BAD_REQUEST)
                                            .build();
                                }

                                // If verification is successful, proceed to build the success
                                // response
                                ResponseCookie accessTokenCookie = ResponseCookie.from(
                                                AuthConstant.CookieType.ACCESS_TOKEN,
                                                (String) tokens.get("access"))
                                        .httpOnly(true)
                                        .path("/")
                                        .maxAge(AuthConstant.ExpiryTime.ACCESS_TOKEN)
                                        .build();

                                ResponseCookie refreshTokenCookie = ResponseCookie.from(
                                                AuthConstant.CookieType.REFRESH_TOKEN,
                                                (String) tokens.get(
                                                        "refresh")) // Corrected cookie name
                                        .httpOnly(true)
                                        .path("/")
                                        .maxAge(AuthConstant.ExpiryTime.REFRESH_TOKEN)
                                        .build();

                                return new ResponseFactory()
                                        .cookie(accessTokenCookie)
                                        .cookie(refreshTokenCookie)
                                        .data(List.of(Map.of(
                                                "accessToken", tokens.get("access"),
                                                "refreshToken", tokens.get("refresh"))))
                                        .status(HttpStatus.OK)
                                        .build();
                            })
                            .onErrorResume(e -> {
                                // Catch errors specifically from authFlow.verify
                                logger.error(
                                        "Error during authFlow verification, reason: {}",
                                        e.getLocalizedMessage());
                                logger.debug("Verification error details: ", e);
                                return new ResponseFactory()
                                        .error(List.of("Verification failed: " + e.getMessage()))
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .build();
                            });
                })
                .onErrorResume(t -> {
                    return new ResponseFactory()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .error(List.of(t.getMessage()))
                            .build();
                })
                .defaultIfEmpty(Objects.requireNonNull(new ResponseFactory()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .error(List.of("Unknown error occured"))
                        .build()
                        .block()));
    }

    @PostMapping("/authz_client")
    public Mono<ResponseEntity<?>> authorizeClient(
            @RequestBody ClientDto client,
            @CookieValue(value = "KF_SESSION_ID", required = false) String sessionId,
            @PathVariable("dimension") String dimension) {
        return authService
                .isClientValid(
                        client.getClientId(),
                        client.getClientSecret(),
                        client.getRedirectUri(),
                        client.getGrantType(),
                        dimension)
                .flatMap((Boolean isValid) -> {
                    if (isValid) {
                        // success
                        logger.info(
                                "Client with id: {}, requested authorization",
                                client.getClientId());
                        return new ResponseFactory().status(HttpStatus.OK).build();
                    } else {
                        return new ResponseFactory()
                                .status(HttpStatus.BAD_REQUEST)
                                .error(List.of("Invalid requester details"))
                                .build();
                    }
                })
                .onErrorResume((Throwable t) -> new ResponseFactory()
                        .status(HttpStatus.BAD_REQUEST)
                        .error(List.of(t.getLocalizedMessage()))
                        .build());
    }

    @PostMapping("/token")
    Mono<ResponseEntity<?>> refreshTokens(
            @RequestBody RefreshTokenDto refreshTokenDto,
            @CookieValue(value = "KF_SESSION_ID", required = false) String sessionId,
            @PathVariable("dimension") String dimension) {

        return this.authService
                .validateAccessToken(refreshTokenDto.getRefreshToken())
                .flatMap(map -> {
                    if (!((boolean) map.get("isValid"))) {
                        return new ResponseFactory()
                                .status(HttpStatus.UNAUTHORIZED)
                                .error(List.of("Invalid session! Please login again"))
                                .build();
                    }

                    return this.authService
                            .generateTokens(refreshTokenDto.getRefreshToken())
                            .flatMap(tokens -> new ResponseFactory()
                                    .cookie(tokens.get(AuthConstant.TokenType.REFRESH))
                                    .cookie(tokens.get(AuthConstant.TokenType.ACCESS))
                                    .status(HttpStatus.OK)
                                    .build());
                });
    }
}
