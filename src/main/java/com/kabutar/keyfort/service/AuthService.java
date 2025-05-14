package com.kabutar.keyfort.service;

import com.kabutar.keyfort.Entity.*;
import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.repository.*;
import com.kabutar.keyfort.util.IDGenerator;
import com.kabutar.keyfort.util.PasswordEncoderUtil;
import com.kabutar.keyfort.util.TokenGenerator;
import com.kabutar.keyfort.util.url.Matcher;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

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
    private JwtService jwtService;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private Matcher matcher;

    /**
     *
     *
     * @return
     */
    public boolean isClientValid(
            String clientId,
            String clientSecret,
            String redirectUri,
            String grantType,
            String dimensionName
    ){
        Client client = clientRepository.findByClientId(clientId);
        Dimension dimension = dimensionRepository.findByName(dimensionName);

        if(
                client.getClientSecret().equals(clientSecret) &&
                matcher.match(redirectUri, clientId) &&
                client.getGrantType().equals(grantType) &&
                dimension.getName().equals(dimensionName)
        ){
           return true;
        }

        return false;
    }

    /**
     *
     * @param username
     * @param password
     * @param clientId
     * @return
     */
    public User matchUserCredential(String username, String password, String clientId, String dimension){
        User user = userRepository.findByUsername(username);

        if(
                user == null || !user.getClient().getClientId().equals(clientId) ||
                !user.getClient().getDimension().getName().equals(dimension)
        ){
            return null;
        }

        List<Credential> credentialList = credentialRepository.findActiveCredentialsForUser(user.getId());

        if(credentialList.size() != 1){
            // user should have 1 active credential
            return null;
        }
        Credential credential = credentialList.get(0);

        if(PasswordEncoderUtil.matches(password,credential.getHash())){
            return user;
        }

        return null;
    }


    /**
     *
     * @param user
     * @return
     */
    public Token getAuthTokenForUser(User user){
        Token token = new Token();
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        token.setToken(TokenGenerator.generateToken128());
        token.setUser(user);
        token.setType(AuthConstant.TokenType.AUTHORIZATION);
        token.setCreatedAt(currentTimestamp);
        token.setValidTill(new Timestamp(currentTimestamp.getTime() + AuthConstant.ExpiryTime.AUTHZ_CODE *1000  ));

        token = tokenRepository.save(token);

        return token;
    }

    /**
     *
     * @param user
     * @param clientId
     * @return
     */
    public User createUser (User user,String clientId){
        Client client = clientRepository.findByClientId(clientId);
        user.setClient(client);
        user = userRepository.save(user);
        return user;
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
    public Map<String,Object> exchangeForTokens(String token,String grantType, String dimension){
        Token savedToken = tokenRepository.findByToken(token);
        
        if(!this.isTokenValid(savedToken)){
            return Map.of("isValid",false);
        }

        if(!savedToken.getType().equals(grantType)){
            return Map.of("isValid",false);
        }

        if(!savedToken.getUser().getClient().getDimension().getName().equals(dimension)){
            return Map.of("isValid",false);
        }

        User user = savedToken.getUser();
        List<String> roles = roleService.getRolesForUser(savedToken.getUser());
        Session session = new Session();
        String accessToken = null;
        String refreshToken = null;
        
        session.setAuthenticated(true);
        session.setUser(user);
        
        //mark current auth token as invalid
        savedToken.setValid(false);
        
        //save to repository
        tokenRepository.save(savedToken);
        sessionRepository.save(session);
        
        
        accessToken = jwtService.generateToken(
                Map.of(
                        AuthConstant.ClaimType.ROLE,roles,
                        AuthConstant.ClaimType.SESSION,session.getId()
                ),
                user.getUsername(),
                AuthConstant.ExpiryTime.ACCESS_TOKEN
        );
        
        refreshToken = jwtService.generateToken(
                Map.of(
                        AuthConstant.ClaimType.ROLE,roles,
                        AuthConstant.ClaimType.SESSION,session.getId()
                ),
                user.getUsername(),
                AuthConstant.ExpiryTime.REFRESH_TOKEN
        );
        
        return Map.of(
                "isValid",true,
                "refresh",refreshToken,
                "access",accessToken
        );
    }

    /**
     *
     * @param jwt
     * @param resourceUrl
     * @return
     */
    public boolean validateAccessToken(String jwt, String resourceUrl,String dimension) {
        Token token = tokenRepository.findByToken(jwt);
        if(!token.getUser().getClient().getDimension().getName().equals(dimension)){
            return false;
        }
        return this.isTokenValid(token);
    }
    
    
    public String getReirectUriWithAuthCode(String uri,String authCode) {
    	StringBuilder sb = new StringBuilder();
    	sb.append(uri);
    	
    	if(uri.contains("?")) {
    		//already has an query
    		sb.append("&auth_code=");
    	}else {
    		sb.append("?auth_code=");
    	}
    	
    	sb.append(authCode);
    	
    	return sb.toString();
    }

}
