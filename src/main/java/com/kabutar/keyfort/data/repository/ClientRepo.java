package com.kabutar.keyfort.data.repository;

import reactor.core.publisher.Mono;

public class ClientRepo extends BaseRepository {

	@Override
	public Mono<Void> create() {
		// TODO Auto-generated method stub
		String CREATE_CLIENT_ENTITY_TABLE_SQL =
			    "CREATE TABLE client_entity (" +
			    "id VARCHAR(255) PRIMARY KEY," +
			    "secret VARCHAR(255) NOT NULL," +
			    "grant_type VARCHAR(255)," +
			    "redirect_uri VARCHAR(2048)," +
			    "name VARCHAR(255) NOT NULL," +
			    "dimension_id VARCHAR(255)" +
			    ");";
		return null;
	}

}
