package com.kabutar.keyfort.data.repository;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.Dimension;

import reactor.core.publisher.Mono;

@Repository
public class DimensionRepo extends BaseRepository<String,Dimension> {
	private final Logger logger = LogManager.getLogger(DimensionRepo.class);
	private final DatabaseClient dbClient;

    public DimensionRepo(DatabaseClient dbClient) {
    	super(Dimension.class,dbClient);
        this.dbClient = dbClient;
    }



	@Override
	public void create() throws Exception {
		logger.info("Entering create method of dimension repository");
        this.createTable(dbClient);
	}



	@Override
	public Mono<String> save() throws Exception {
		// TODO Auto-generated method stub
		Dimension d = new Dimension();
        d.setIsActive(true);
        d.setCreatedAt(LocalDateTime.now());
        d.setUpdatedAt(LocalDateTime.now());
        d.setCreatedBy("admin");
        d.setIsDeleted(false);
        d.setDeletedAt(null);
        d.setDisplayName("abc");
        d.setName("ABC");
        d.setUpdatedBy("admin");
        return this.insertIntoTable(d);
	}
    
    

}
