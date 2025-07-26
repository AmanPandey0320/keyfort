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
	public Mono<Void> create() throws Exception {
        String CREATE_DIMENSIONS_TABLE_SQL = this.getCreateSQL(DimensionEntity.class);

        logger.info("Attempting to create table with SQL: {}", CREATE_DIMENSIONS_TABLE_SQL);

        return databaseClient.sql(CREATE_DIMENSIONS_TABLE_SQL).fetch().rowsUpdated().doOnSuccess(rows -> {
            
            logger.info("Created table dimensions (rowsUpdated: {})", rows);
        })
        .doOnError(e -> {
            
            logger.error("Error creating dimensions table, reason: {}", e.getMessage());
            logger.debug("Full exception: ", e); // Log full stack trace for debug
             throw new RuntimeException("Failed to create dimensions table", e);
        })
        .then();
	}
    
    

}
