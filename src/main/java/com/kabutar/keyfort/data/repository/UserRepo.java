package com.kabutar.keyfort.data.repository;

import com.kabutar.keyfort.data.entity.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
	public Mono<User> save(User u) throws Exception {
		if(u.getId() == null){
            return  this.insertIntoTable(u).flatMap(id -> {
                u.setId(UUID.fromString(id));
                return Mono.just(u);
            });
        }
		return this.updateTable(u).flatMap((i) -> Mono.just(u));
	}

    public Flux<User> getUsersByClient(Client c){
        DatabaseClient.GenericExecuteSpec spec = this.databaseClient.sql("SELECT * FROM users WHERE client_id=:cid").bind("cid",c.getId());
        return this.getAll(spec,User.class);
    }

    public Mono<User> getUserByUserName(String username){
        DatabaseClient.GenericExecuteSpec spec = this.databaseClient.sql("SELECT * FROM users WHERE username=:username").bind("username",username);
        return this.getOne(spec,User.class);
    }



}
