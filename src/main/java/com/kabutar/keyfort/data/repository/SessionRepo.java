package com.kabutar.keyfort.data.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.Session;

@Repository
public class SessionRepo extends BaseRepository {
	private final Logger logger = LogManager.getLogger(SessionRepo.class);
	private final DatabaseClient databaseClient;
	
	public SessionRepo(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

	@Override
	public void create() throws Exception {
		logger.info("Entering create method of session repository");
		this.createTable(Session.class, this.databaseClient);
	}

}
