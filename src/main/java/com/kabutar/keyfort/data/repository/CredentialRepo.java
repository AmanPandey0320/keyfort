package com.kabutar.keyfort.data.repository;

import com.kabutar.keyfort.data.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.Credential;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class CredentialRepo extends BaseRepository<String, Credential> {
	private final Logger logger = LogManager.getLogger(CredentialRepo.class);
	private final DatabaseClient databaseClient;
	
	public CredentialRepo(DatabaseClient databaseClient) {
		super(Credential.class,databaseClient);
        this.databaseClient = databaseClient;
	}

	@Override
	public void create() throws Exception {
		logger.debug("Entering create method of credential repository");
		this.createTable(this.databaseClient);
		
	}

	@Override
	public Mono<Credential> save(Credential c) throws Exception {
        if(c.getId() == null) {
            return this.insertIntoTable(c).flatMap(id -> {
                c.setId(UUID.fromString(id));
                return Mono.just(c);
            });
        }
        return this.updateTable(c).flatMap(id -> Mono.just(c));
	}

    public Flux<Credential> getAllActiveCredentialsByUser(User u){
        DatabaseClient.GenericExecuteSpec spec = this.databaseClient.sql("SELECT * FROM credentials WHERE user_id=:uid").bind("uid",u.getId());
        return this.getAll(spec, Credential.class);
    }

}
