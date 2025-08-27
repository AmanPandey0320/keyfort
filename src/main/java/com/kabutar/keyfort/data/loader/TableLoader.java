package com.kabutar.keyfort.data.loader;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
		d.setId("1ba61787-b1f5-4a35-8038-a33004034138");
      d.setIsActive(true);
      d.setCreatedAt(LocalDateTime.now());
      d.setUpdatedAt(LocalDateTime.now());
      d.setCreatedBy("admin");
      d.setIsDeleted(false);
      d.setDeletedAt(null);
      d.setDisplayName("abcd");
      d.setName("ABCD");
      d.setUpdatedBy("admin");
      this.dimensionRepo.save(d)
      .subscribe(id -> logger.info("Inserted id: {}",id));
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
