package com.kabutar.keyfort.service;

import com.kabutar.keyfort.Entity.Client;
import com.kabutar.keyfort.Entity.Credential;
import com.kabutar.keyfort.Entity.Token;
import com.kabutar.keyfort.Entity.User;
import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.repository.ClientRepository;
import com.kabutar.keyfort.repository.CredentialRepository;
import com.kabutar.keyfort.repository.TokenRepository;
import com.kabutar.keyfort.repository.UserRepository;
import com.kabutar.keyfort.util.PasswordEncoderUtil;
import com.kabutar.keyfort.util.TokenGenerator;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private JwtService jwtService;

    @Autowired
    private RoleService roleService;

    /**
     *
     *
     * @return
     */
    public boolean isClientValid(
            String clientId,
            String clientSecret,
            String redirectUri,
            String grantType
    ){
        Client client = clientRepository.findByClientId(clientId);

        if(
                client.getClientSecret().equals(clientSecret) &&
                client.getRedirectUri().equals(redirectUri) &&
                client.getGrantType().equals(grantType)
        ){
            //success
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
    public User matchUserCredential(String username, String password, String clientId){
        User user = userRepository.findByUsername(username);

        if(user == null || !user.getClient().getClientId().equals(clientId)){
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
        User user = userRepository.findById(userId);

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
    public Map<String,Object> exchangeForTokens(String token,String grantType){
        Token savedToken = tokenRepository.findByToken(token);

        if(!this.isTokenValid(savedToken)){
            return Map.of("isValid",false);
        }

        if(!savedToken.getType().equals(grantType)){
            return Map.of("isValid",false);
        }

        String clientId = null;
        String userName = null;
        List<String> roles = null;
        Token refreshToken = new Token();
        Token accessToken = new Token();
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        if(savedToken.getType().equals(AuthConstant.TokenType.REFRESH)){
            Claims claims = jwtService.extractAllClaim(token);
            clientId = (String) claims.get(AuthConstant.ClaimType.CLIENT);
            userName = claims.getSubject();
            roles = (List<String>) claims.get(AuthConstant.ClaimType.ROLE);



            if(!savedToken.getUser().getClient().getClientId().equals(clientId)){
                return Map.of("isValid",false);
            }

            if(!savedToken.getUser().getUsername().equals(userName)){
                return Map.of("isValid",false);
            }
        }else{
            clientId = savedToken.getUser().getClient().getClientId();
            userName = savedToken.getUser().getUsername();
            roles = roleService.getRolesForUser(savedToken.getUser());
        }

        refreshToken.setToken(jwtService.generateToken(
                Map.of(
                        AuthConstant.ClaimType.ROLE,roles,
                        AuthConstant.ClaimType.CLIENT,clientId
                ),
                userName,
                AuthConstant.ExpiryTime.REFRESH_TOKEN
        ));
        refreshToken.setType(AuthConstant.TokenType.REFRESH);
        refreshToken.setUser(savedToken.getUser());
        refreshToken.setCreatedAt(currentTimestamp);
        refreshToken.setValidTill(new Timestamp(currentTimestamp.getTime() + AuthConstant.ExpiryTime.REFRESH_TOKEN *1000  ));

        accessToken.setToken(jwtService.generateToken(
                Map.of(
                        AuthConstant.ClaimType.ROLE,roles,
                        AuthConstant.ClaimType.CLIENT,clientId
                ),
                userName,
                AuthConstant.ExpiryTime.ACCESS_TOKEN
        ));
        accessToken.setType(AuthConstant.TokenType.ACCESS);
        accessToken.setUser(savedToken.getUser());
        accessToken.setValidTill(currentTimestamp);
        accessToken.setValidTill(new Timestamp(currentTimestamp.getTime() + AuthConstant.ExpiryTime.ACCESS_TOKEN *1000  ));

        savedToken.setValid(false);

        //save tokens
        accessToken = tokenRepository.save(accessToken);
        refreshToken = tokenRepository.save(refreshToken);
        tokenRepository.save(savedToken);


        return Map.of(
                "isValid",true,
                "refresh",refreshToken.getToken(),
                "access",accessToken.getToken()
        );
    }

    /**
     *
     * @param token
     * @return
     */
    public boolean validateAccessToken(String jwt, String resourceUrl){
        Token token = tokenRepository.findByToken(jwt);

        return this.isTokenValid(token);
    }

}
