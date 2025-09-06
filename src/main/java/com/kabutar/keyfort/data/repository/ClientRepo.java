package com.kabutar.keyfort.data.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.Client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


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
	public Mono<Client> save(Client c) throws Exception {
        if(c.getId() == null) {
            return this.insertIntoTable(c).flatMap(id -> {
                c.setId(UUID.fromString(id));
                return Mono.just(c);
            });
        }
        return this.updateTable(c).flatMap(id -> Mono.just(c));
    }

    public Flux<Client> getClientsByDimension(UUID dimensionId){
        String GET_CLIENTS_BY_DIMENSION_SQL = "SELECT * from clients WHERE dimension_id=:did";
        DatabaseClient.GenericExecuteSpec spec = this.databaseClient.sql(GET_CLIENTS_BY_DIMENSION_SQL).bind("did",dimensionId);
        return this.getAll(spec, Client.class);
    }
}
