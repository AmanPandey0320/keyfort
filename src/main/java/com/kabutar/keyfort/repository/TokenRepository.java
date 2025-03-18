package com.kabutar.keyfort.repository;

import com.kabutar.keyfort.Entity.Credential;
import com.kabutar.keyfort.Entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Credential,Integer> {
    public Token save (Token token);

    @Query(
            value = "SELECT * FROM token as t WHERE t.token = :token",
            nativeQuery = true
    )
    public Token findByToken(@Param("token") String token);

}
