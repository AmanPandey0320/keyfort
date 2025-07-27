package com.kabutar.keyfort.data.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.Client;


@Repository
public class ClientRepo extends BaseRepository {
	private final Logger logger = LogManager.getLogger(ClientRepo.class);
	private final DatabaseClient databaseClient;
	
	public ClientRepo(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

	@Override
	public void create() throws Exception {
		logger.info("Entering create method of client repository");
		this.createTable(Client.class, this.databaseClient);
	}

}
