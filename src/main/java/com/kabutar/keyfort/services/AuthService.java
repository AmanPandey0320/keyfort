package com.kabutar.keyfort.services;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

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

    /**
     *
     * @param client
     * @return
     */
    public boolean isClientValid(Client client){
        Client savedClient = clientRepository.findByClientId(client.getClientId());

        if(
                savedClient.getClientSecret().equals(client.getClientSecret()) &&
                        savedClient.getRedirectUri().equals(client.getRedirectUri()) &&
                        savedClient.getGrantType().equals(client.getGrantType())
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
        token.setValidTill(new Timestamp(currentTimestamp.getTime() + 5*60*100 + 200 ));

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


}
