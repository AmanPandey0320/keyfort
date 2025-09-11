package com.kabutar.keyfort.data.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.Token;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class TokenRepo extends BaseRepository<UUID,Token> {
	private final Logger logger = LogManager.getLogger(TokenRepo.class);
	private final DatabaseClient databaseClient;
	
	public TokenRepo(DatabaseClient databaseClient) {
		super(Token.class,databaseClient);
        this.databaseClient = databaseClient;
	}

	@Override
	public void create() throws Exception {
		logger.debug("Entering create method of token repository");
		this.createTable(this.databaseClient);
		
	}

	@Override
	public Mono<Token> save(Token t) throws Exception {
        if(t.getId() == null) {
            return this.insertIntoTable(t).flatMap(id -> {
                t.setId(id);
                return Mono.just(t);
            });
        }
        return this.updateTable(t).flatMap(id -> Mono.just(t));
	}

}
