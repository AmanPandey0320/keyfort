package com.kabutar.keyfort.data.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.DimensionEntity;

@Repository
public class DimensionRepo extends BaseRepository {
	private final Logger logger = LogManager.getLogger(DimensionRepo.class);
	private final DatabaseClient dbClient;

    public DimensionRepo(DatabaseClient dbClient) {
        this.dbClient = dbClient;
    }



	@Override
	public void create() throws Exception {
		logger.info("Entering create method of dimension repository");
        this.createTable(DimensionEntity.class, dbClient);
	}
    
    

}
