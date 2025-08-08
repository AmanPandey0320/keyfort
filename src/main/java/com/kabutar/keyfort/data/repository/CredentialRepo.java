package com.kabutar.keyfort.data.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.Credentials;

@Repository
public class CredentialRepo extends BaseRepository {
	private final Logger logger = LogManager.getLogger(CredentialRepo.class);
	private final DatabaseClient databaseClient;
	
	public CredentialRepo(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
	}

	@Override
	public void create() throws Exception {
		logger.debug("Entering create method of credential repository");
		this.createTable(Credentials.class, this.databaseClient);
		
	}

}
