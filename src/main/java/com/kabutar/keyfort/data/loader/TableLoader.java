package com.kabutar.keyfort.data.loader;

import java.time.LocalDateTime;

import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.data.entity.Client;
import com.kabutar.keyfort.data.entity.Credential;
import com.kabutar.keyfort.data.entity.User;
import com.kabutar.keyfort.util.PasswordEncoderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kabutar.keyfort.data.entity.Dimension;
import com.kabutar.keyfort.data.repository.ClientRepo;
import com.kabutar.keyfort.data.repository.CredentialRepo;
import com.kabutar.keyfort.data.repository.DimensionRepo;
import com.kabutar.keyfort.data.repository.SessionRepo;
import com.kabutar.keyfort.data.repository.TokenRepo;
import com.kabutar.keyfort.data.repository.UserRepo;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Component
public class TableLoader implements DefaultLoader {
	
	private final Logger logger = LogManager.getLogger(TableLoader.class);
	
	@Value("${config.dimension.name}")
	private String dimensionName;
	
	@Value("${config.dimension.displayName}")
	private String dimensionDisplayName;
	
	
	@Value("${config.client.secret}")
	private String clientSecret;
	
	@Value("${config.client.redirectUri}")
	private String redirectUri;

    @Value("${config.client.name}")
    private String clientName;
	
	@Value("${config.admin.userName}")
	private String userName;
	
	@Value("${config.admin.lastName}")
	private String lastName;
	
	@Value("${config.admin.firstName}")
	private String firstName;
	
	@Value("${config.admin.password}")
	private String password;

    @Value("${config.admin.email}")
    private String email;
	
	@Autowired
	private DimensionRepo dimensionRepo;
	
	@Autowired
	private ClientRepo clientRepo;
	
	@Autowired
	private CredentialRepo credentialRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private SessionRepo sessionRepo;
	
	@Autowired
	private TokenRepo tokenRepo;

	private void createTables() throws Exception{
		this.dimensionRepo.create();
		this.clientRepo.create();
		this.userRepo.create();
		this.credentialRepo.create();
		this.sessionRepo.create();
		this.tokenRepo.create();
		
	}

    /**
     * saves password
     * @param u
     * @return
     */
    private Mono<Credential> populateCredential(User u){
        logger.info("Populating credential for user: {}",u.getUsername());
        return this.credentialRepo.getAllActiveCredentialsByUser(u).collectList().flatMap(credentials -> {
            for(Credential credential:credentials){
                if( credential.getIsActive() && !credential.getIsDeleted() && PasswordEncoderUtil.matches(this.password,credential.getHash())){
                    logger.info("Same password already active, no need to save");
                    return Mono.just(credential);
                }else{
                    credential.setIsDeleted(true);
                    credential.setIsActive(false);

                    try {
                        this.credentialRepo.save(credential);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            Credential credential = new Credential();

            credential.setIsActive(true);
            credential.setIsDeleted(false);
            credential.setHash(PasswordEncoderUtil.encodePassword(this.password));
            credential.setUserId(u.getId());

            try {
                return this.credentialRepo.save(credential);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    /**
     * populates user
     * @param c
     * @return
     */
    private Mono<User> populateUser(Client c){
        logger.info("Populating default user");
        return this.userRepo.getUserByUserName(this.userName).flatMap(user -> {
            if(user.isVerified() && user.getClientId().equals(c.getId()) && !user.getIsDeleted()){
                logger.info("User with the user name exist, for the provided client, updating other info");
                user.setFirstName(this.firstName);
                user.setLastName(this.lastName);

                try {
                    logger.info("Updating new user with username: {}",this.userName);
                    return this.userRepo.save(user);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            logger.info("Delete old user with user name: {}",user.getUsername());
            return this.userRepo.deleteById(user.getId()).flatMap(count -> {
                User u = new User();
                u.setFirstName(this.firstName);
                u.setLastName(this.lastName);
                u.setUsername(this.userName);
                u.setClientId(c.getId());
                u.setVerified(true);
                u.setEmail(this.email);

                try {
                    logger.info("Saving new user with username: {}",this.userName);
                    return this.userRepo.save(u);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });


        }).switchIfEmpty(Mono.defer(() -> {
            logger.info("No User found with username: {}, so creating new",this.userName);
            User u = new User();
            u.setFirstName(this.firstName);
            u.setLastName(this.lastName);
            u.setUsername(this.userName);
            u.setClientId(c.getId());
            u.setVerified(true);
            u.setEmail(this.email);

            try {
                logger.info("Saving new user with username: {}, as user don't exist",this.userName);
                return this.userRepo.save(u);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

    /**
     * populates default client in database
     * @param d
     * @return
     */
    private Mono<Client> populateClient(Dimension d){
        logger.info("Populating default client...");
        return this.clientRepo.getClientsByDimension(d.getId()).collectList().flatMap(clients -> {
            if(clients.size() == 1){
                logger.info("Single client present for the dimension id: {}",d.getId().toString());
                Client c = clients.getFirst();
                c.setUpdatedAt(LocalDateTime.now());
                c.setRedirectUri(this.redirectUri);
                c.setName(this.clientName);
                c.setSecret(this.clientSecret);
                c.setGrantType(AuthConstant.GrantType.AUTH_CODE);

                try {
                    logger.info("Updating existing client with id: {}, with new data",c.getId());
                    return this.clientRepo.save(c);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            logger.info("Multiple or no client found, creating fresh client, deleting old ones if any");

            try {
                return this.clientRepo.deleteAll().flatMap(deletedRows -> {
                    logger.info("{} old clients found",deletedRows);
                    Client c = new Client();
                    c.setName(this.clientName);
                    c.setSecret(this.clientSecret);
                    c.setRedirectUri(this.redirectUri);
                    c.setDimensionId(d.getId());
                    c.setGrantType(AuthConstant.GrantType.AUTH_CODE);

                    try {

                        logger.info("Saving new client for dimension id: {}",d.getId());
                        return this.clientRepo.save(c);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });

    }

    /**
     * populates default dimension in database
     * @return
     */
	private Mono<Dimension> populateDimension() {
		logger.info("Entering populate data");
	    return this.dimensionRepo.getDimensionByName(dimensionName).doOnNext(d -> {
	    	logger.info("Dimension present in database, creating one with name: {}, id is: {}",this.dimensionName,d.getId());
	    })
	    .switchIfEmpty(Mono.defer(() -> {
	    	logger.debug("Dimension not present in database, creating one with name: {}",this.dimensionName);
            Dimension d = new Dimension();
            d.setIsActive(true);
            d.setDisplayName(dimensionDisplayName);
            d.setName(dimensionName);
            try {
				return this.dimensionRepo.save(d).doOnNext(savedDimension -> logger.info("Created dimension with id: {}",savedDimension.getId()));
			} catch (Exception e) {
				logger.error("Error details while saving dimension while startup: {}",e);
                throw new RuntimeException(e);
			}
        }));
	}



	@Override
	@PostConstruct
	public void loadData() {
		try {
			this.createTables();
			this.populateDimension()
                    .flatMap(this::populateClient)
                    .flatMap(this::populateUser)
                    .flatMap(this::populateCredential)
                    .subscribe(
                            d -> {logger.info("Default values admin populated");},
                            e -> {
                                logger.error("Error while populating default admin");
                                e.printStackTrace();
                                System.exit(-1);
                            }
                    );
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
