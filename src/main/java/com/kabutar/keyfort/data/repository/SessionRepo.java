package com.kabutar.keyfort.data.repository;

import com.kabutar.keyfort.data.entity.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class SessionRepo extends BaseRepository<UUID, Session> {
    private final Logger logger = LogManager.getLogger(SessionRepo.class);
    private final DatabaseClient databaseClient;

    public SessionRepo(DatabaseClient databaseClient) {
        super(Session.class, databaseClient);
        this.databaseClient = databaseClient;
    }

    @Override
    public void create() throws Exception {
        logger.info("Entering create method of session repository");
        this.createTable(this.databaseClient);
    }

    @Override
    public Mono<Session> save(Session s) throws Exception {
        if (s.getId() == null) {
            return this.insertIntoTable(s).flatMap(id -> {
                s.setId(id);
                return Mono.just(s);
            });
        }
        return this.updateTable(s).flatMap(id -> Mono.just(s));
    }
}
