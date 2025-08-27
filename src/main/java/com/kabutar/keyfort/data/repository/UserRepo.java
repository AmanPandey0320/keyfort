package com.kabutar.keyfort.data.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.User;

import reactor.core.publisher.Mono;

@Repository
public class UserRepo extends BaseRepository<String,User> {
	private final Logger logger = LogManager.getLogger(UserRepo.class);
	private final DatabaseClient databaseClient;
	
	public UserRepo(DatabaseClient databaseClient) {
		super(User.class,databaseClient);
        this.databaseClient = databaseClient;
	}

	@Override
	public void create() throws Exception {
		logger.debug("Entering create method of user repository");
		this.createTable(this.databaseClient);
		
	}

	@Override
	public Mono<String> save(User u) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
