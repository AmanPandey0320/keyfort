package com.kabutar.keyfort.data.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.Client;

import reactor.core.publisher.Mono;


@Repository
public class ClientRepo extends BaseRepository<String, Client> {
	private final Logger logger = LogManager.getLogger(ClientRepo.class);
	private final DatabaseClient databaseClient;
	
	public ClientRepo(DatabaseClient databaseClient) {
		super(Client.class,databaseClient);
        this.databaseClient = databaseClient;
    }

	@Override
	public void create() throws Exception {
		logger.info("Entering create method of client repository");
		this.createTable(this.databaseClient);
	}

	@Override
	public Mono<String> save(Client c) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
