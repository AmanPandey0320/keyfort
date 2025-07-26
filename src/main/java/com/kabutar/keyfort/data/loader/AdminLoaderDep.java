package com.kabutar.keyfort.data.loader;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.constant.DataConstant;
import com.kabutar.keyfort.data.entity.Credential;
import com.kabutar.keyfort.data.entity.Dimension;
import com.kabutar.keyfort.data.entity.DimensionEntity;
import com.kabutar.keyfort.data.entity.User;
import com.kabutar.keyfort.data.entity.client.Client;
import com.kabutar.keyfort.data.repository.ClientRepository;
import com.kabutar.keyfort.data.repository.CredentialRepository;
import com.kabutar.keyfort.data.repository.DimensionRepo;
import com.kabutar.keyfort.data.repository.DimensionRepository;
import com.kabutar.keyfort.data.repository.SessionRepository;
import com.kabutar.keyfort.data.repository.TokenRepository;
import com.kabutar.keyfort.data.repository.UserRepository;
import com.kabutar.keyfort.util.PasswordEncoderUtil;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Component()
public class AdminLoaderDep implements DefaultLoader {
//public class AdminLoader  {
	
	private static Logger logger = LogManager.getLogger(AdminLoaderDep.class);
	
	@Autowired
	private DimensionRepo dimensionRepo;
//	
//	@Value("${config.dimension.name}")
//	private String dimensionName;
//	
//	@Value("${config.dimension.displayName}")
//	private String dimensionDisplayName;
//	
//	
//	@Value("${config.client.secret}")
//	private String clientSecret;
//	
//	@Value("${config.client.redirectUri}")
//	private String redirectUri;
//	
//	@Value("${config.admin.userName}")
//	private String userName;
//	
//	@Value("${config.admin.lastName}")
//	private String lastName;
//	
//	@Value("${config.admin.firstName}")
//	private String firstName;
//	
//	@Value("${config.admin.password}")
//	private String password;
//	
//	@Autowired
//	private ClientRepository clientRepository;
//	
//	@Autowired
//	private DimensionRepository dimensionRepository;
//	
//	@Autowired
//	private UserRepository userRepository;
//	
//	@Autowired
//	private CredentialRepository credentialRepository;
//	
//	@Autowired
//	private SessionRepository sessionRepository;
//	
//	@Autowired
//	private TokenRepository tokenRepository;
//	
//
//	
//	
	private Mono<DimensionEntity> checkAndCreateDimension() {
		Mono<DimensionEntity> dimension = dimensionRepo.findByName(DataConstant.DEFAULT_DIMENSION_NAME);
		
		
		
		
//		if(dimension == null) {
//			dimension = new DimensionEn
//			
//			//config dimension
//			dimension.setName(dimensionName);
//			dimension.setDisplayName(dimensionDisplayName);
//			dimension.setActive(true);
//			
//			dimensionRepository.save(dimension);
//		}
//		
		return dimension;
	}
//	
//	@Transactional
//	private Client checkAndCreateClient(Dimension dimension) {
//		List<Client> clients = clientRepository.findClientByDimension(dimension);
//		Client client = null;
//		
//		logger.debug("clients for dimension {} are {} in numbers",dimension.getName(),clients.size());
//		
//		for(Client c: clients) {
//			if(c.getName().equals(DataConstant.DEFAULT_CLIENT_NAME)) {
//				client = c;
//				break;
//			}
//		}
//		
//		if(client != null) {
//			client.setClientSecret(clientSecret);
//			client.setRedirectUri(redirectUri);
//		}else {
//			client = new Client();
//			client.setClientSecret(clientSecret);
//			client.setRedirectUri(redirectUri);
//			client.setDimension(dimension);
//			client.setName(DataConstant.DEFAULT_CLIENT_NAME);
//			client.setGrantType(AuthConstant.GrantType.AUTH_CODE);
//		}
//		
//		clientRepository.save(client);
//		
//		return client;
//	}
//	
//	private void checkAndCreateUser(Client client) {
//		User user = null;
//		List<User> users = userRepository.findByClient(client);
//		
//		for(User u: users) {
//			if(u.getUsername().equals(userName)) {
//				user  = u;
//				break;
//			}
//		}
//		
//		if(user == null) {
//			user = new User();
//			user.setUsername(userName);
//			user.setFirstName(firstName);
//			user.setLastName(lastName);
//			user.setClient(client);
//			user.setVerified(true);
//			
//			userRepository.save(user);
//			
//			logger.info("Created user with id: {}",user.getId());
//			
//		}else {
//			//incativate all previous passwords
//			credentialRepository.setAllUserCredentialsInactive(user.getId());
//		}
//		
//		
//		
//		Credential credential = new Credential();
//		
//		//set credentials
//		credential.setActive(true);
//		credential.setDeleted(false);
//		credential.setHash(PasswordEncoderUtil.encodePassword(password));
//		credential.setUser(user);
//		
//		credentialRepository.save(credential);
//
//	}
//	
//	private void clearData() {
//		sessionRepository.deleteAll();
//		tokenRepository.deleteAll();
//	}
//	
	@Override
	public void loadData() {
		try {
//			this.clearData();
//			Dimension dimension = this.checkAndCreateDimension();
			this.checkAndCreateDimension();
//			
//			//check existing data
//			dimension = dimensionRepository.findByName(DataConstant.DEFAULT_DIMENSION_NAME);
//			
//			logger.info("Created dimension with id: {}",dimension.getId());
//			
//			//config client
//			Client client = this.checkAndCreateClient(dimension);
//			
//			logger.info("Created client {} with id: {}",client.getName(),client.getClientId());
//			
//			//admin user config
//			this.checkAndCreateUser(client);
			
		}catch(Exception e) {
			logger.error("Error creating default values, reason: {}",e.getLocalizedMessage());
			e.printStackTrace();
			
			System.exit(0);
		}
		
	}

}
