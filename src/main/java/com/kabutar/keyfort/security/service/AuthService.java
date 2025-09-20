package com.kabutar.keyfort.security.service;

import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.data.entity.*;
import com.kabutar.keyfort.data.repository.*;
import com.kabutar.keyfort.util.Jwt;
import com.kabutar.keyfort.util.PasswordEncoderUtil;
import com.kabutar.keyfort.util.TokenGenerator;
import com.kabutar.keyfort.util.url.Matcher;

import io.jsonwebtoken.Claims;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final Logger logger = LogManager.getLogger(AuthService.class);

    @Autowired
    private Matcher matcher;

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private DimensionRepo dimensionRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CredentialRepo credentialRepo;

    @Autowired
    private TokenRepo tokenRepo;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private RoleService roleService;

    @Autowired
    private Jwt jwt;
    /**
     * @param clientId
     * @param redirectUri
     * @return
     * @throws Exception
     */
    public Mono<Client> matchRedirectUri(String clientId, String redirectUri) throws Exception {
        return clientRepo.getById(UUID.fromString(clientId)).flatMap(client -> {
            if (this.matcher.match(redirectUri, clientId)) {
                return Mono.just(client);
            }
            return Mono.error(new Exception("Invalid client"));
        });
    }

    /**
     * @return
     * @throws Exception
     */
    public Mono<Boolean> isClientValid(
            String clientId,
            String clientSecret,
            String redirectUri,
            String grantType,
            String dimensionName) {
        try {
            return this.matchRedirectUri(clientId, redirectUri)
                    .flatMap((Client client) -> {
                        if (client.getSecret().equals(clientSecret)
                                && client.getGrantType().equals(grantType)) {
                            return this.dimensionRepo
                                    .getDimensionByName(dimensionName)
                                    .flatMap(dimension ->
                                            Mono.just(dimension.getName().equals(dimensionName)));
                        }
                        return Mono.just(false);
                    })
                    .onErrorResume(Exception.class, (e) -> {
                        logger.error(
                                "Error while validation client, Reason: {}",
                                e.getLocalizedMessage());
                        logger.debug(e);
                        return Mono.just(false);
                    })
                    .defaultIfEmpty(false);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.debug(e);
            return Mono.error(e);
        }
    }
    //
    /**
     * @param username
     * @param password
     * @param clientId
     * @return
     */
    public Mono<User> matchUserCredential(String username, String password) {
        return this.userRepo
                .getUserByUserName(username)
                .flatMap(user -> {
                    logger.debug("User with username: {} found", user.getUsername());
                    return this.credentialRepo
                            .getAllActiveCredentialsByUser(user)
                            .collectList()
                            .flatMap(credentials -> {
                                if (credentials.size() != 1) {
                                    logger.info(
                                            "User with username: {} has multiple or no active credentials",
                                            username);
                                    logger.debug(
                                            "User with username: {} has {} active credentials",
                                            username,
                                            credentials.size());
                                    Mono.empty();
                                }
                                Credential credential = credentials.getFirst();

                                if (PasswordEncoderUtil.matches(password, credential.getHash())) {
                                    logger.info(
                                            "User with username: {} login success for valid credentials",
                                            username);
                                    return Mono.just(user);
                                }
                                logger.info(
                                        "User with username: {} login failed for invalid credentials",
                                        username);
                                return Mono.empty();
                            });
                })
                .switchIfEmpty(Mono.defer(() -> Mono.empty()));
    }

    /**
     * @param user
     * @return
     */
    public Mono<Token> getAuthTokenForUser(User user) throws Exception {
        Token token = new Token();

        token.setValid(true);
        token.setValidTill(LocalDateTime.now().plusSeconds(AuthConstant.ExpiryTime.AUTHZ_CODE));
        token.setUserId(user.getId());
        token.setType(AuthConstant.TokenType.AUTHORIZATION);
        token.setToken(TokenGenerator.generateToken128());

        return this.tokenRepo.save(token);
    }
    //
    /**
     * @param token
     * @return
     */
    private boolean isTokenValid(Token token) {
        if (token == null) {
            return false;
        }

        if (!token.isValid()) {
            return false;
        }

        if (token.getValidTill().isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }

    /**
     * @param token
     * @param clientSecret
     * @param dimension
     * @param sessionId
     * @return
     */
    public Mono<Map<String, Object>> exchangeForTokens(
            String token, String clientSecret, String dimension, String sessionId) {
        return Mono.zip(
                        this.tokenRepo.getDetailsOfToken(token),
                        this.sessionRepo.getById(UUID.fromString(sessionId)))
                .flatMap(objects -> {
                    Token savedToken = objects.getT1();
                    Session session = objects.getT2();
                    boolean hasSession = true;

                    boolean isValid = true;
                    List<String> errors = new ArrayList<>();

                    if (!this.isTokenValid(savedToken)) {
                        logger.warn("Authentication code is not valid");
                        errors.add("Session timed out!");
                        isValid = false;
                    }

                    if (isValid
                            && !savedToken.getType().equals(AuthConstant.TokenType.AUTHORIZATION)) {
                        logger.warn("Authentication code does not have appropriate grant");
                        errors.add("Invalid token!");
                        isValid = false;
                    }

                    try {
                        if (isValid && !savedToken.get("dimension").equals(dimension)) {
                            logger.warn(
                                    "Authentication code comes from invalid requester {}",
                                    dimension);
                            errors.add("Invalid requester!");
                            isValid = false;
                        }

                        if (isValid && !savedToken.get("secret").equals(clientSecret)) {
                            logger.warn(
                                    "Authentication code comes from invalid requester {} with incorrect client secret",
                                    dimension);
                            errors.add("Invalid requester!");
                            isValid = false;
                        }

                        if (!isValid) {
                            return Mono.just(Map.of("isValid", isValid, "errors", errors));
                        }

                        // make current token valid as false;
                        savedToken.setValid(false);
                        this.tokenRepo.save(savedToken).subscribe();

                        session.setUserId(savedToken.getUserId());
                        session.setAuthenticated(true);
                        session.setLastUsed(LocalDateTime.now());

                        // save session as authenticated
                        this.sessionRepo.save(session).subscribe();

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    return roleService.getRolesForUser(savedToken.getUserId()).flatMap(roles -> {
                        String accessToken = null;
                        String refreshToken = null;
                        accessToken = jwt.generateToken(
                                Map.of(
                                        AuthConstant.ClaimType.ROLE, roles,
                                        AuthConstant.ClaimType.SESSION, sessionId),
                                savedToken.get("username").toString(),
                                AuthConstant.ExpiryTime.ACCESS_TOKEN);

                        refreshToken = jwt.generateToken(
                                Map.of(
                                        AuthConstant.ClaimType.ROLE, roles,
                                        AuthConstant.ClaimType.SESSION, sessionId),
                                savedToken.get("username").toString(),
                                AuthConstant.ExpiryTime.REFRESH_TOKEN);

                        logger.debug(
                                "access token and new session generated for user: {}",
                                savedToken.get("username").toString());

                        return Mono.just(Map.of(
                                "isValid", true,
                                "refresh", refreshToken,
                                "access", accessToken,
                                "errors", errors));
                    });
                });
    }

    /**
     * @param accessToken
     * @return
     */
    public Mono<Boolean> validateAccessToken(String accessToken) {
        Claims claims = jwt.extractAllClaim(accessToken);

        String userName = claims.getSubject();
        String sessionId = (String) claims.get(AuthConstant.ClaimType.SESSION);

        logger.debug("Validation session id {}", sessionId);

        return this.sessionRepo.getById(UUID.fromString(sessionId)).flatMap(session -> {
            logger.debug("recieved session id {}", session.getId());

            if (session.getLastUsed()
                    .plusSeconds(AuthConstant.ExpiryTime.SESSION)
                    .isBefore(LocalDateTime.now())) {
                return Mono.just(false);
            }

            return this.userRepo.getById(session.getUserId()).flatMap(user -> {
                if (!user.getUsername().equals(userName)) {
                    return Mono.just(false);
                }

                // TODO: roles check to be implemented
                return Mono.just(true);
            });
        });
    }

    /**
     * @param refreshToken
     * @return
     */
    public Mono<Map<String, ResponseCookie>> generateTokens(String refreshToken) {
        Claims claims = jwt.extractAllClaim(refreshToken);

        List<String> roles = (List<String>) claims.get(AuthConstant.ClaimType.ROLE);
        String sessionId = (String) claims.get(AuthConstant.ClaimType.SESSION);
        String username = claims.getSubject();

        String accessToken = jwt.generateToken(
                Map.of(
                        AuthConstant.ClaimType.ROLE, roles,
                        AuthConstant.ClaimType.SESSION, sessionId),
                username,
                AuthConstant.ExpiryTime.ACCESS_TOKEN);
        String newRefreshToken = jwt.generateToken(
                Map.of(
                        AuthConstant.ClaimType.ROLE, roles,
                        AuthConstant.ClaimType.SESSION, sessionId),
                username,
                AuthConstant.ExpiryTime.REFRESH_TOKEN);

        // Cookie
        ResponseCookie accessTokenCookie = ResponseCookie.from(
                        AuthConstant.CookieType.ACCESS_TOKEN, accessToken)
                .httpOnly(true)
                .path("/")
                .maxAge(AuthConstant.ExpiryTime.ACCESS_TOKEN)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from(
                        AuthConstant.CookieType.REFRESH_TOKEN, newRefreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(AuthConstant.ExpiryTime.REFRESH_TOKEN)
                .build();

        return Mono.just(Map.of(
                AuthConstant.TokenType.REFRESH, refreshTokenCookie,
                AuthConstant.TokenType.ACCESS, accessTokenCookie));
    }
}
