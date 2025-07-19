package com.kabutar.keyfort.data.repository;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.DimensionEntity;

import reactor.core.publisher.Flux;

@Repository
public class DimensionRepo {
	
	private final DatabaseClient databaseClient;

    public DimensionRepo(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }
    
    public Flux<DimensionEntity> findAll() {
        String sql = "SELECT * FROM dimension";

        return databaseClient.sql(sql)
                .map((row, metadata) -> DimensionEntity.getEntity(row))
                .all();
    }

}
