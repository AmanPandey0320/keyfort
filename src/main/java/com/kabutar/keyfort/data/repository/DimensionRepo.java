package com.kabutar.keyfort.data.repository;

import com.kabutar.keyfort.data.entity.Dimension;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class DimensionRepo extends BaseRepository<UUID, Dimension> {
    private final Logger logger = LogManager.getLogger(DimensionRepo.class);
    private final DatabaseClient dbClient;

    public DimensionRepo(DatabaseClient dbClient) {
        super(Dimension.class, dbClient);
        this.dbClient = dbClient;
    }

    @Override
    public void create() throws Exception {
        logger.info("Entering create method of dimension repository");
        this.createTable(dbClient);
    }

    @Override
    public Mono<Dimension> save(Dimension d) throws Exception {
        if (d.getId() == null) {
            return this.insertIntoTable(d).flatMap(id -> {
                d.setId(id);
                return Mono.just(d);
            });
        }
        return this.updateTable(d).flatMap(id -> Mono.just(d));
    }

    public Mono<Dimension> getDimensionByName(String name) {
        logger.info("Getting dimension by name {}", name);
        String GET_BY_NAME_SQL = "SELECT * FROM dimensions WHERE name = :name";
        DatabaseClient.GenericExecuteSpec spec =
                this.dbClient.sql(GET_BY_NAME_SQL).bind("name", name);
        return this.getOne(spec, Dimension.class);
    }
}
