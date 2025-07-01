package com.kabutar.keyfort.security.service;

import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.data.entity.*;
import com.kabutar.keyfort.data.entity.client.Client;
import com.kabutar.keyfort.data.repository.*;
import com.kabutar.keyfort.util.Jwt;
import com.kabutar.keyfort.util.PasswordEncoderUtil;
import com.kabutar.keyfort.util.TokenGenerator;
import com.kabutar.keyfort.util.url.Matcher;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class AuthService {
	
	private final Logger logger = LogManager.getLogger(AuthService.class);

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private DimensionRepository dimensionRepository;
    
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private Jwt jwt;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private Matcher matcher;
    
    /**
     * 
     * @param clientId
     * @param redirectUri
     * @return
     * @throws Exception
     */
    public Mono<Client> matchRedirectUri(String clientId, String redirectUri) throws Exception {
    	Client client = clientRepository.findByClientId(clientId);
    	
    	//if redirect uri matches
    	if(matcher.match(redirectUri, clientId)) {
    		return Mono.just(client);
    	}else{
    		return Mono.error(new Exception("Invalid client"));
    	}
    }

    /**
     *
     *
     * @return
     * @throws Exception 
     */
    public Mono<Boolean> isClientValid(
            String clientId,
            String clientSecret,
            String redirectUri,
            String grantType,
            String dimensionName
    ){
        try {
			return this.matchRedirectUri(clientId, redirectUri).flatMap((Client client) -> {
				Dimension dimension = dimensionRepository.findByName(dimensionName);

			    if(
			            client.getClientSecret().equals(clientSecret) &&
			            client.getGrantType().equals(grantType) &&
			            dimension.getName().equals(dimensionName)
			    ){
			       return Mono.just(true);
			    }
			    return Mono.just(false);
			}).onErrorResume(Exception.class, (e) -> {
				logger.error("Error while validation client, Reason: {}",e.getLocalizedMessage());
				logger.debug(e);
				return Mono.just(false);
			}).defaultIfEmpty(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug(e);
			return Mono.error(e);
		}
        
        
    }

    /**
     *
     * @param username
     * @param password
     * @param clientId
     * @return
     */
    public Mono<User> matchUserCredential(String username, String password){
        User user = userRepository.findByUsername(username);

        if(user == null){
        	logger.info("User with username: {} login failed for invalid dimension or clienti-id",username);
            return Mono.empty();
        }
       
        logger.debug("User with username: {} found",user.getUsername());
        

        List<Credential> credentialList = credentialRepository.findActiveCredentialsForUser(user.getId());

        if(credentialList.size() != 1){
            // user should have 1 active credential
        	logger.info("User with username: {} has multiple or no active credentials",username);
        	logger.debug("User with username: {} has {} active credentials",username,credentialList.size());
            Mono.empty();
        }
        Credential credential = credentialList.get(0);

        if(PasswordEncoderUtil.matches(password,credential.getHash())){
        	logger.info("User with username: {} login success for valid credentials",username);
            return Mono.just(user);
        }

        logger.info("User with username: {} login failed for invalid credentials",username);
        return Mono.empty();
    }


    /**
     *
     * @param user
     * @return
     */
    public Mono<Token> getAuthTokenForUser(User user){
        Token token = new Token();
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        token.setToken(TokenGenerator.generateToken128());
        token.setUser(user);
        token.setType(AuthConstant.TokenType.AUTHORIZATION);
        token.setCreatedAt(currentTimestamp);
        token.setValidTill(new Timestamp(currentTimestamp.getTime() + AuthConstant.ExpiryTime.AUTHZ_CODE *1000  ));

        token = tokenRepository.save(token);
        
        logger.debug("User with username: {} has an authcode",user.getUsername());

        return Mono.just(token);
    }

    /**
     *
     * @param userId
     * @param password
     * @return
     */
    public Credential createCredential(String userId, String password){
        Credential credential = new Credential();
        Optional<User> userOp = userRepository.findById(userId);
        
        if(userOp.isEmpty()) {
        	return null;
        }
        
        User user = userOp.get();

        credential.setUser(user);
        credential.setHash(PasswordEncoderUtil.encodePassword(password));
        credential.setActive(true);

        credentialRepository.setAllUserCredentialsInactive(user.getId());
        credential = credentialRepository.save(credential);
        
        logger.debug("Credential created for user: {}",user.getUsername());

        return credential;

    }

    /**
     *
     * @param token
     * @return
     */
    private boolean isTokenValid(Token token){
        if(token == null){
            return false;
        }

        if(!token.isValid()){
            return false;
        }

        if(token.getValidTill().getTime() < System.currentTimeMillis()){
            return false;
        }

        return true;
    }

    /**
     *
     * @param token
     * @param grantType
     * @return
     */
    public Mono<Map<String,Object>> exchangeForTokens(String token,String clientSecret, String dimension, String sessionId){
        Token savedToken = tokenRepository.findByToken(token);
        boolean isValid = true;
        List<String> errors = new ArrayList<>();
        
        if(isValid && !this.isTokenValid(savedToken)){
        	logger.warn("Authentication code {} is not valid",token);
        	errors.add("Invalid user");
            isValid = false;
        }

        if(isValid && !savedToken.getType().equals(AuthConstant.TokenType.AUTHORIZATION)){
        	logger.warn("Authentication code {} does not have valid grants",token);
        	errors.add("Invalid grants");
        	isValid = false;
        }

        if(isValid && !savedToken.getUser().getClient().getDimension().getName().equals(dimension)){
        	logger.warn("Authentication code {} does not belong to current dimension {}",token,dimension);
        	errors.add("Invalid user or dimension");
        	isValid = false;
        }
        
        if(isValid && !savedToken.getUser().getClient().getClientSecret().equals(clientSecret)) {
        	logger.warn("Authentication code {} does not have valid client secret",token);
        	errors.add("Invalid client");
        	isValid = false;
        }
        
        if(!isValid) {
        	return Mono.just(Map.of("isValid",isValid, "errors",errors));
        }
        
        if(sessionId == null) {
        	logger.warn("Authentication code {} does not have valid session",token);
        	errors.add("Session timeout");
        	isValid = false;
        }

        User user = savedToken.getUser();
        //mark current auth token as invalid
        savedToken.setValid(false);
        
        //save to repository
        tokenRepository.save(savedToken);
        
        return roleService.getRolesForUser(savedToken.getUser()).flatMap(roles -> {
        	String accessToken = null;
            String refreshToken = null;
            accessToken = jwt.generateToken(
                    Map.of(
                            AuthConstant.ClaimType.ROLE,roles,
                            AuthConstant.ClaimType.SESSION,sessionId
                    ),
                    user.getUsername(),
                    AuthConstant.ExpiryTime.ACCESS_TOKEN
            );
            
            refreshToken = jwt.generateToken(
                    Map.of(
                            AuthConstant.ClaimType.ROLE,roles,
                            AuthConstant.ClaimType.SESSION,sessionId
                    ),
                    user.getUsername(),
                    AuthConstant.ExpiryTime.REFRESH_TOKEN
            );
            
            logger.debug("access token and new session generated for user: {}",user.getUsername());
            
            return Mono.just(Map.of(
                    "isValid",true,
                    "refresh",refreshToken,
                    "access",accessToken,
                    "errors",errors
            ));
        });
        
        
        
    }

    /**
     *
     * @param jwt
     * @param resourceUrl
     * @return
     */
    @Transactional
    public boolean validateAccessToken(String accessToken) {
        Claims claims = jwt.extractAllClaim(accessToken);
        
        String userName = claims.getSubject();
        String sessionId = (String) claims.get(AuthConstant.ClaimType.SESSION);
        
        logger.debug("Validation session id {}",sessionId);
        
        Session session = sessionRepository.getReferenceById(sessionId);
        logger.debug("recieved session id {}",session.getId());
        
        if(!(session.getId().equals(sessionId) && session.getUser().getUsername().equals(userName))) {
        	return false;
        }
        
        if((session.getLastUsed().getTime() + AuthConstant.ExpiryTime.ACCESS_TOKEN * 1000) <= System.currentTimeMillis()) {
        	return false;
        }
        
        //roles check to be implemented as part of rbac
        
        return true;
    }

}
