package com.kabutar.keyfort.data.loader;

import java.time.LocalDateTime;

import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.data.entity.Client;
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
			this.populateDimension().flatMap(this::populateClient).subscribe();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

}
