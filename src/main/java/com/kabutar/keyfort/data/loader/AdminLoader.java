package com.kabutar.keyfort.data.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.constant.DataConstant;
import com.kabutar.keyfort.data.entity.Client;
import com.kabutar.keyfort.data.entity.Credential;
import com.kabutar.keyfort.data.entity.Dimension;
import com.kabutar.keyfort.data.entity.User;
import com.kabutar.keyfort.data.repository.ClientRepository;
import com.kabutar.keyfort.data.repository.CredentialRepository;
import com.kabutar.keyfort.data.repository.DimensionRepository;
import com.kabutar.keyfort.data.repository.SessionRepository;
import com.kabutar.keyfort.data.repository.TokenRepository;
import com.kabutar.keyfort.data.repository.UserRepository;
import com.kabutar.keyfort.util.PasswordEncoderUtil;

import jakarta.annotation.PostConstruct;

@Component()
public class AdminLoader implements DefaultDataLoader {
	
	private static Logger logger = LogManager.getLogger(AdminLoader.class);
	
	@Value("${config.dimension.id}")
	private String dimensionId;
	
	@Value("${config.client.secret}")
	private String clientSecret;
	
	@Value("${config.client.redirectUri}")
	private String redirectUri;
	
	@Value("${config.admin.userName}")
	private String userName;
	
	@Value("${config.admin.lastName}")
	private String lastName;
	
	@Value("${config.admin.firstName}")
	private String firstName;
	
	@Value("${config.admin.password}")
	private String password;
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	private DimensionRepository dimensionRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CredentialRepository credentialRepository;
	
	@Autowired
	private SessionRepository sessionRepository;
	
	@Autowired
	private TokenRepository tokenRepository;
	

	
	
	private void checkData() {
		//TODO: implement data check, remove delete
		this.credentialRepository.deleteAll();
		this.tokenRepository.deleteAll();
		this.sessionRepository.deleteAll();
		this.userRepository.deleteAll();
		this.clientRepository.deleteAll();
		this.dimensionRepository.deleteAll();
	}
	
	@Override
	@PostConstruct
	public void loadData() {
		Dimension dimension = new Dimension();
		Client client = new Client();
		User user = new User();
		Credential credential = new Credential();
		
		try {
			
			//check existing data
			this.checkData();
			
			//config dimension
			dimension.setName(DataConstant.DEFAULT_DIMENSION_NAME);
			dimension.setDisplayName(DataConstant.DEFAULT_DIMENSION_DISPLAY_NAME);
			dimension.setActive(true);
			
			dimensionRepository.save(dimension);
			
			logger.info("Created dimension with id: {}",dimension.getId());
			
			//config client
			client.setClientSecret(clientSecret);
			client.setRedirectUri(redirectUri);
			client.setDimension(dimension);
			client.setName(DataConstant.DEFAULT_CLIENT_NAME);
			client.setGrantType(AuthConstant.GrantType.AUTH_CODE);
			
			clientRepository.save(client);
			
			logger.info("Created client {} with id: {}",client.getName(),client.getClientId());
			
			//admin user config
			user.setUsername(userName);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setClient(client);
			user.setVerified(true);
			
			userRepository.save(user);
			
			logger.info("Created user with id: {}",user.getId());
			
			//set credentials
			credential.setActive(true);
			credential.setDeleted(false);
			credential.setHash(PasswordEncoderUtil.encodePassword(password));
			credential.setUser(user);
			
			credentialRepository.save(credential);
			
		}catch(Exception e) {
			logger.error("Error creating default values, reason: {}",e.getLocalizedMessage());
			e.printStackTrace();
			
			System.exit(0);
		}
		
	}

}
