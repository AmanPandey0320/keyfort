package com.kabutar.keyfort.data.loader;

import java.time.LocalDateTime;

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
	
	private void populateData() throws Exception {
		Dimension d = new Dimension();
		d.setIsActive(true);
		d.setCreatedAt(LocalDateTime.now());
		d.setUpdatedAt(LocalDateTime.now());
		d.setCreatedBy("SYSTEM");
		d.setIsDeleted(false);
		d.setDeletedAt(null);
		d.setDisplayName(dimensionDisplayName);
		d.setName(dimensionName);
		d.setUpdatedBy("SYSTEM");
		this.dimensionRepo.save(d)
			.subscribe(id -> logger.info("Inserted dimension id: {}",id));
	}

	@Override
	@PostConstruct
	public void loadData() {
		try {
			this.createTables();
			this.populateData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}

}
