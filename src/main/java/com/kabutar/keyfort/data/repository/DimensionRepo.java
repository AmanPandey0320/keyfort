package com.kabutar.keyfort.data.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.DimensionEntity;
import com.kabutar.keyfort.security.service.AuthService;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class DimensionRepo extends BaseRepository {
	private final Logger logger = LogManager.getLogger(DimensionRepo.class);
	private final DatabaseClient databaseClient;

    public DimensionRepo(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }
    
    public Flux<DimensionEntity> findAll() {
        String sql = "SELECT * FROM dimensions";

        return databaseClient.sql(sql)
                .map((row, metadata) -> new DimensionEntity(row))
                .all();
    }
    
    public Mono<DimensionEntity> findByName(String name) {
        String sql = "SELECT * FROM dimensions WHERE name = :name";

        return databaseClient.sql(sql)
                .bind("name", name)
                .map((row, metadata) -> new DimensionEntity(row))
                .one();
    }
    
    public Mono<Long> save(DimensionEntity entity) {
        String sql = "INSERT INTO dimensions " +
                "(id, name, display_name, is_active, created_by, updated_by, created_at, updated_at) " +
                "VALUES (:id, :name, :display_name, :is_active, :created_by, :updated_by, :created_at, :updated_at)";

        return databaseClient.sql(sql)
                .bind("id", entity.getId())
                .bind("name", entity.getName())
                .bind("display_name", entity.getDisplayName())
                .bind("is_active", entity.isActive())
                .bind("created_by", entity.getCreatedBy())
                .bind("updated_by", entity.getUpdatedBy())
                .fetch()
                .rowsUpdated();
    }



	@Override
	@PostConstruct
	public void create() {
        String CREATE_DIMENSIONS_TABLE_SQL =
                "CREATE TABLE IF NOT EXISTS dimensions (" +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "created_by VARCHAR(255)," +
                "updated_by VARCHAR(255)," +
                "id VARCHAR(255) PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL UNIQUE," +
                "display_name VARCHAR(255)," +
                "is_active BOOLEAN NOT NULL DEFAULT TRUE" +
                ");";

        logger.info("Attempting to create table with SQL: {}", CREATE_DIMENSIONS_TABLE_SQL);

        databaseClient.sql(CREATE_DIMENSIONS_TABLE_SQL).fetch().rowsUpdated().subscribe((item) -> {
        	logger.info("Created table dimensions");
        },
        (e) -> {
        	logger.error("Error creating dimensions table, reasom: {}",e.getMessage());
        	logger.debug(e);
        	System.exit(0);
        });
	}
    
    

}
