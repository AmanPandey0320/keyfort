package com.kabutar.keyfort.data.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.Session;

import reactor.core.publisher.Mono;

@Repository
public class SessionRepo extends BaseRepository {
	private final Logger logger = LogManager.getLogger(SessionRepo.class);
	private final DatabaseClient databaseClient;
	
	public SessionRepo(DatabaseClient databaseClient) {
		super(Session.class,databaseClient);
        this.databaseClient = databaseClient;
    }

	@Override
	public void create() throws Exception {
		logger.info("Entering create method of session repository");
		this.createTable(this.databaseClient);
	}

	@Override
	public Mono<String> save() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
