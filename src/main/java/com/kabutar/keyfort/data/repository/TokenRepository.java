package com.kabutar.keyfort.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.Credential;
import com.kabutar.keyfort.data.entity.Token;

@Repository
public interface TokenRepository extends JpaRepository<Credential,String> {
    public Token save (Token token);

    @Query(
            value = "SELECT * FROM token as t WHERE t.token = :token",
            nativeQuery = true
    )
    public Token findByToken(@Param("token") String token);

}
